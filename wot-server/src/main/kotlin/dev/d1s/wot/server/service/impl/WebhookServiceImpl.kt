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

import com.soywiz.krypto.SHA1
import dev.d1s.advice.exception.UnprocessableEntityException
import dev.d1s.advice.util.orElseNotFound
import dev.d1s.lp.server.publisher.AsyncLongPollingEventPublisher
import dev.d1s.teabag.dto.DtoConverter
import dev.d1s.teabag.dto.DtoListConverterFacade
import dev.d1s.teabag.dto.EntityWithDto
import dev.d1s.teabag.dto.EntityWithDtoList
import dev.d1s.teabag.dto.util.convertToDtoIf
import dev.d1s.teabag.dto.util.convertToDtoListIf
import dev.d1s.teabag.dto.util.converterForList
import dev.d1s.wot.server.constant.*
import dev.d1s.wot.server.dto.webhook.WebhookDto
import dev.d1s.wot.server.entity.webhook.Webhook
import dev.d1s.wot.server.entity.webhook.WebhookNonce
import dev.d1s.wot.server.factory.TelegramBotFactory
import dev.d1s.wot.server.repository.WebhookRepository
import dev.d1s.wot.server.service.TargetService
import dev.d1s.wot.server.service.WebhookService
import dev.d1s.wot.server.util.idString
import dev.inmo.tgbotapi.types.chat.Chat
import org.lighthousegames.logging.logging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.ThreadLocalRandom

@Service
class WebhookServiceImpl : WebhookService {

    @set:Autowired
    lateinit var webhookRepository: WebhookRepository

    @set:Autowired
    lateinit var webhookDtoConverter: DtoConverter<WebhookDto, Webhook>

    @set:Autowired
    lateinit var publisher: AsyncLongPollingEventPublisher

    @set:Autowired
    lateinit var targetService: TargetService

    @set:Autowired
    lateinit var telegramBotFactory: TelegramBotFactory

    @set:Lazy
    @set:Autowired
    lateinit var webhookServiceImpl: WebhookServiceImpl

    private val webhookListDtoConverter: DtoListConverterFacade<WebhookDto, Webhook> by lazy {
        webhookDtoConverter.converterForList()
    }

    private val log = logging()

    @Transactional
    override fun createWebhook(webhook: Webhook): EntityWithDto<Webhook, WebhookDto> {
        log.d {
            "Creating webhook $webhook."
        }

        webhook.nonce = this.createNonce().nonce

        val savedWebhook = webhookRepository.save(webhook)

        // init request executor...
        telegramBotFactory.getTelegramBot(webhook)

        log.d {
            "Successfully created webhook $webhook."
        }

        val webhookDto = webhookDtoConverter.convertToDto(savedWebhook)

        publisher.publish(
            WEBHOOK_CREATED_GROUP,
            requireNotNull(savedWebhook.id),
            webhookDto
        )

        return savedWebhook to webhookDto
    }

    @Transactional(readOnly = true)
    override fun getWebhook(id: String, requireDto: Boolean): EntityWithDto<Webhook, WebhookDto> {
        val webhook = webhookRepository.findWebhookByIdOrNameOrNonce(id)
            .orElseNotFound(NOT_FOUND_MESSAGE)

        return webhook to webhookDtoConverter.convertToDtoIf(webhook, requireDto)
    }

    @Transactional(readOnly = true)
    override fun getWebhookByNonce(nonce: String, requireDto: Boolean): EntityWithDto<Webhook, WebhookDto> {
        val webhook = webhookRepository.findWebhookByNonce(nonce)
            .orElseNotFound(NOT_FOUND_MESSAGE)

        return webhook to webhookDtoConverter.convertToDtoIf(webhook, requireDto)
    }

    @Transactional(readOnly = true)
    override fun getAllWebhooks(requireDto: Boolean): EntityWithDtoList<Webhook, WebhookDto> {
        val webhooks = webhookRepository.findAll()

        return webhooks to webhookListDtoConverter.convertToDtoListIf(webhooks, requireDto)
    }

    @Transactional
    override fun updateWebhook(id: String, webhook: Webhook): EntityWithDto<Webhook, WebhookDto> {
        log.d {
            "Updating webhook $id with new data $webhook."
        }

        webhookServiceImpl.checkForCollision(webhook)

        val (existingWebhook, _) = webhookServiceImpl.getWebhook(id)

        telegramBotFactory.dememoizeAndStopTelegramBot(existingWebhook)

        existingWebhook.name = webhook.name
        existingWebhook.botToken = webhook.botToken
        existingWebhook.private = webhook.private
        existingWebhook.targets = webhook.targets

        val savedWebhook = webhookRepository.save(existingWebhook)

        telegramBotFactory.getTelegramBot(existingWebhook)

        log.d {
            "Successfully updated webhook $savedWebhook."
        }

        val webhookDto = webhookDtoConverter.convertToDto(savedWebhook)

        publisher.publish(
            WEBHOOK_UPDATED_GROUP,
            requireNotNull(savedWebhook.id),
            webhookDto
        )

        return savedWebhook to webhookDto
    }

    @Transactional
    override fun deleteWebhook(id: String) {
        log.d {
            "Deleting webhook $id."
        }

        val (existingWebhook, webhookDto) = webhookServiceImpl.getWebhook(id, true)

        telegramBotFactory.dememoizeAndStopTelegramBot(existingWebhook)

        webhookRepository.delete(existingWebhook)

        log.d {
            "Successfully deleted webhook $existingWebhook."
        }

        publisher.publish(
            WEBHOOK_DELETED_GROUP,
            requireNotNull(existingWebhook.id),
            webhookDto
        )
    }

    @Transactional
    override fun subscribe(webhook: Webhook, chat: Chat) {
        log.d {
            "Subscribing ${chat.idString} to webhook $webhook."
        }

        webhook.targets += targetService.createOrGetTargetForChar(chat.idString)

        val savedWebhook = webhookRepository.save(webhook)

        log.d {
            "Successfully subscribed the chat to webhook $webhook."
        }

        publisher.publish(
            WEBHOOK_SUBSCRIBED_GROUP,
            requireNotNull(savedWebhook.id),
            webhookDtoConverter.convertToDto(savedWebhook)
        )
    }

    @Transactional
    override fun unsubscribe(webhook: Webhook, chat: Chat) {
        log.d {
            "Unsubscribing ${chat.idString} from webhook $webhook."
        }

        val (target, _) = targetService.getTarget(chat.idString)

        webhook.targets -= target

        val savedWebhook = webhookRepository.save(webhook)

        log.d {
            "Successfully unsubscribed the chat from webhook $webhook."
        }

        publisher.publish(
            WEBHOOK_UNSUBSCRIBED_GROUP,
            requireNotNull(savedWebhook.id),
            webhookDtoConverter.convertToDto(savedWebhook)
        )
    }

    @Transactional(readOnly = true)
    override fun isSubscribed(webhook: Webhook, chat: Chat): Boolean =
        webhook.targets.map {
            it.chatId
        }.contains(chat.idString)

    override fun hasAccess(chat: Chat, to: Webhook): Boolean =
        !to.private || webhookServiceImpl.isSubscribed(to, chat)

    @Transactional
    override fun regenerateNonce(webhook: Webhook): WebhookNonce {
        val webhookNonce = this.createNonce()

        webhook.nonce = webhookNonce.nonce

        val savedWebhook = webhookRepository.save(webhook)

        publisher.publish(
            WEBHOOK_NONCE_REGENERATED_GROUP,
            requireNotNull(savedWebhook.id),
            webhookNonce
        )

        return webhookNonce
    }

    @Transactional(readOnly = true)
    override fun checkForCollision(webhook: Webhook) {
        val collidingSubject = when {
            webhookRepository.findWebhookByName(webhook.name).isPresent -> "name"
            webhookRepository.findWebhookByBotToken(webhook.botToken).isPresent -> "bot token"
            else -> null
        }

        collidingSubject?.let {
            throw UnprocessableEntityException("Webhook with the same $it already exists.")
        }
    }

    private fun createNonce(): WebhookNonce {
        val buffer = ByteArray(BUFFER_SIZE)

        ThreadLocalRandom.current().nextBytes(buffer)

        return WebhookNonce(
            SHA1.digest(buffer).hex
        )
    }

    private companion object {

        private const val NOT_FOUND_MESSAGE = "Webhook not found."

        private const val BUFFER_SIZE = 1024
    }
}