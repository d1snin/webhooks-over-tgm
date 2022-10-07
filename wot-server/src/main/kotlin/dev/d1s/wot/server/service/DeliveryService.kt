/*
 * Copyright 2022 Mikhail Titov and other Webhooks over Telegram project contributors
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

package dev.d1s.wot.server.service

import cc.popkorn.annotations.Injectable
import cc.popkorn.injecting
import dev.d1s.teabag.dto.DtoConverter
import dev.d1s.teabag.dto.EntityWithDto
import dev.d1s.teabag.dto.ktorm.ExportedSequenceWithDto
import dev.d1s.teabag.dto.ktorm.convertElementsToDtoIf
import dev.d1s.teabag.ktorm.DEFAULT_LIMIT
import dev.d1s.teabag.ktorm.DEFAULT_OFFSET
import dev.d1s.teabag.stdlib.exception.EntityNotFoundException
import dev.d1s.wot.commons.dto.delivery.DeliveryDto
import dev.d1s.wot.server.bot.factory.getTelegramBot
import dev.d1s.wot.server.entity.Delivery
import dev.d1s.wot.server.exception.DeliverySendingFailedException
import dev.d1s.wot.server.exception.InvalidContentException
import dev.d1s.wot.server.repository.DeliveryRepository
import dev.d1s.wot.server.util.converter.toTextSource
import dev.inmo.micro_utils.coroutines.IO
import dev.inmo.tgbotapi.extensions.api.send.sendMessage
import dev.inmo.tgbotapi.extensions.api.send.withTypingAction
import dev.inmo.tgbotapi.types.ChatId
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.lighthousegames.logging.logging

interface DeliveryService {

    suspend fun createDelivery(delivery: Delivery): EntityWithDto<Delivery, DeliveryDto>

    suspend fun getDeliveryById(id: String, requireDto: Boolean = false): EntityWithDto<Delivery, DeliveryDto>

    suspend fun getDeliveries(
        limit: Int = DEFAULT_LIMIT,
        offset: Int = DEFAULT_OFFSET,
        requireDto: Boolean = false
    ): ExportedSequenceWithDto<Delivery, DeliveryDto>

    suspend fun deleteDelivery(id: String): Delivery
}

@Injectable
class DeliveryServiceImpl : DeliveryService {

    private val log = logging()

    private val errorHandlingScope = CoroutineScope(IO)

    private val deliveryRepository by injecting<DeliveryRepository>()

    private val webhookService by injecting<WebhookService>()

    private val targetService by injecting<TargetService>()

    private val deliveryDtoConverter by injecting<DtoConverter<DeliveryDto, Delivery>>()

    override suspend fun createDelivery(delivery: Delivery): EntityWithDto<Delivery, DeliveryDto> {
        log.d {
            "Creating delivery $delivery"
        }

        val webhook = delivery.webhook

        webhookService.checkWebhookAvailability(webhook)

        val bot = getTelegramBot(webhook)

        log.d {
            "Publishing messages for targets"
        }

        coroutineScope {
            val targets = targetService.getWebhookTargetsByWebhook(webhook)

            targets.forEach {
                val exceptionHandler = CoroutineExceptionHandler { _, t ->
                    delivery.successful = false

                    errorHandlingScope.launch {
                        targetService.makeTargetUnavailable(it)
                    }

                    throw DeliverySendingFailedException()
                }

                if (it.available) {
                    launch(exceptionHandler) {
                        val chat = ChatId(it.chatId.toLong())

                        val telegramBot = bot.bot

                        telegramBot.withTypingAction(chat) {
                            val content = delivery.content.sources.map { source ->
                                source.toTextSource() ?: throw InvalidContentException()
                            }

                            telegramBot.sendMessage(chat, content)
                        }
                    }
                }
            }
        }

        deliveryRepository.add(delivery)

        val deliveryDto = deliveryDtoConverter.convertToDto(delivery)

        return delivery to deliveryDto
    }

    override suspend fun getDeliveryById(id: String, requireDto: Boolean): EntityWithDto<Delivery, DeliveryDto> {
        val delivery = deliveryRepository.findDeliveryById(id) ?: throw EntityNotFoundException()

        return delivery to deliveryDtoConverter.convertToDto(delivery)
    }

    override suspend fun getDeliveries(
        limit: Int,
        offset: Int,
        requireDto: Boolean
    ) = deliveryRepository.findAllDeliveries(limit, offset).let {
        it to deliveryDtoConverter.convertElementsToDtoIf(it, requireDto)
    }

    override suspend fun deleteDelivery(id: String): Delivery {
        val (delivery, _) = this.getDeliveryById(id)

        log.d {
            "Deleting delivery $delivery"
        }

        return delivery.apply {
            delete()
        }
    }
}