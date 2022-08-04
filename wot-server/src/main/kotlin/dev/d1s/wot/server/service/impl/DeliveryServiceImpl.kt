/*
 * Copyright 2022 Webhooks over Telegram project contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.d1s.wot.server.service.impl

import dev.d1s.advice.exception.NotFoundException
import dev.d1s.lp.server.publisher.AsyncLongPollingEventPublisher
import dev.d1s.teabag.dto.DtoConverter
import dev.d1s.teabag.dto.EntityWithDto
import dev.d1s.teabag.dto.EntityWithDtoList
import dev.d1s.teabag.dto.util.convertToDtoIf
import dev.d1s.teabag.dto.util.convertToDtoListIf
import dev.d1s.wot.commons.constant.DELIVERY_CREATED_GROUP
import dev.d1s.wot.commons.constant.DELIVERY_DELETED_GROUP
import dev.d1s.wot.commons.dto.delivery.DeliveryDto
import dev.d1s.wot.server.converter.textSource.TextSourceConverter
import dev.d1s.wot.server.entity.delivery.Delivery
import dev.d1s.wot.server.factory.TelegramBotFactory
import dev.d1s.wot.server.repository.DeliveryRepository
import dev.d1s.wot.server.service.DeliveryService
import dev.d1s.wot.server.service.TargetService
import dev.d1s.wot.server.service.WebhookService
import dev.inmo.tgbotapi.extensions.api.send.sendMessage
import dev.inmo.tgbotapi.extensions.api.send.withTypingAction
import dev.inmo.tgbotapi.types.ChatId
import kotlinx.coroutines.*
import org.lighthousegames.logging.logging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class DeliveryServiceImpl : DeliveryService {

    @set:Autowired
    lateinit var deliveryRepository: DeliveryRepository

    @set:Autowired
    lateinit var deliveryDtoConverter: DtoConverter<DeliveryDto, Delivery>

    @set:Autowired
    lateinit var publisher: AsyncLongPollingEventPublisher

    @set:Autowired
    lateinit var telegramBotFactory: TelegramBotFactory

    @set:Autowired
    lateinit var textSourceConverter: TextSourceConverter

    @set:Autowired
    lateinit var targetService: TargetService

    @set:Autowired
    lateinit var webhookService: WebhookService

    @set:Lazy
    @set:Autowired
    lateinit var deliveryServiceImpl: DeliveryServiceImpl

    private val log = logging()

    @Transactional
    override suspend fun createDelivery(delivery: Delivery): EntityWithDto<Delivery, DeliveryDto> {
        log.d {
            "Creating delivery $delivery."
        }

        val webhook = delivery.webhook

        webhookService.checkAvailability(webhook)

        val bot = telegramBotFactory.getTelegramBot(webhook).bot

        log.d {
            "Publishing messages for targets."
        }

        coroutineScope {
            webhook.targets.forEach {
                if (it.available) {
                    launch(CoroutineExceptionHandler { _, t ->
                        t.printStackTrace()
                        delivery.successful = false
                        targetService.makeUnavailable(it)
                    }) {
                        val chat = ChatId(it.chatId.toLong())

                        bot.withTypingAction(chat) {
                            bot.sendMessage(chat, delivery.content.sources.map { source ->
                                textSourceConverter.convert(source)
                            })
                        }
                    }
                }
            }
        }

        if (delivery.successful != false) {
            delivery.successful = true
        }

        delivery.time = Instant.now()

        val savedDelivery = withContext(Dispatchers.IO) {
            deliveryRepository.save(delivery)
        }

        log.d {
            "Successfully created delivery $savedDelivery."
        }

        val deliveryDto = deliveryDtoConverter.convertToDto(savedDelivery)

        publisher.publish(
            DELIVERY_CREATED_GROUP, requireNotNull(webhook.id), deliveryDto
        )

        return savedDelivery to deliveryDto
    }

    @Transactional(readOnly = true)
    override fun getDeliveryById(id: String, requireDto: Boolean): EntityWithDto<Delivery, DeliveryDto> {
        val delivery = deliveryRepository.findById(id).orElseThrow {
            NotFoundException("Delivery not found.")
        }

        return delivery to deliveryDtoConverter.convertToDtoIf(delivery, requireDto)
    }

    @Transactional(readOnly = true)
    override fun getAllDeliveries(requireDto: Boolean): EntityWithDtoList<Delivery, DeliveryDto> {
        val deliveries = deliveryRepository.findAll()

        return deliveries to deliveryDtoConverter.convertToDtoListIf(deliveries, requireDto)
    }

    @Transactional
    override fun deleteDelivery(id: String) {
        log.d {
            "Deleting delivery $id."
        }

        val (existingDelivery, deliveryDto) = deliveryServiceImpl.getDeliveryById(id, true)

        deliveryRepository.delete(existingDelivery)

        log.d {
            "Successfully deleted delivery $existingDelivery"
        }

        publisher.publish(
            DELIVERY_DELETED_GROUP, requireNotNull(existingDelivery.webhook.id), deliveryDto
        )
    }
}