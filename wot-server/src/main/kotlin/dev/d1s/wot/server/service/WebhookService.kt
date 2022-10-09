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
import com.soywiz.krypto.encoding.hex
import dev.d1s.teabag.dto.DtoConverter
import dev.d1s.teabag.dto.EntityWithDto
import dev.d1s.teabag.dto.convertToDtoIf
import dev.d1s.teabag.dto.ktorm.ExportedSequenceWithDto
import dev.d1s.teabag.dto.ktorm.convertElementsToDtoIf
import dev.d1s.teabag.ktorm.DEFAULT_LIMIT
import dev.d1s.teabag.ktorm.DEFAULT_OFFSET
import dev.d1s.teabag.postgresql.handlePsqlUniqueViolationOrThrow
import dev.d1s.teabag.stdlib.exception.EntityNotFoundException
import dev.d1s.wot.commons.dto.webhook.WebhookDto
import dev.d1s.wot.commons.entity.webhook.WebhookNonce
import dev.d1s.wot.commons.entity.webhook.WebhookState
import dev.d1s.wot.server.bot.factory.TelegramBotFactory
import dev.d1s.wot.server.entity.Target
import dev.d1s.wot.server.entity.Webhook
import dev.d1s.wot.server.entity.WebhookTarget
import dev.d1s.wot.server.exception.UnavailableWebhookException
import dev.d1s.wot.server.repository.WebhookRepository
import dev.d1s.wot.server.repository.WebhookTargetRepository
import dev.d1s.wot.server.util.idString
import dev.inmo.tgbotapi.types.chat.Chat
import org.lighthousegames.logging.logging
import java.util.concurrent.ThreadLocalRandom

interface WebhookService {

    suspend fun createWebhook(webhook: Webhook): EntityWithDto<Webhook, WebhookDto>

    suspend fun getWebhook(id: String, requireDto: Boolean = false): EntityWithDto<Webhook, WebhookDto>

    suspend fun getWebhookTargetsByTarget(target: Target): List<Webhook>

    suspend fun getWebhookByNonce(nonce: String, requireDto: Boolean = false): EntityWithDto<Webhook, WebhookDto>

    suspend fun getWebhooks(
        limit: Int?,
        offset: Int?,
        requireDto: Boolean = false
    ): ExportedSequenceWithDto<Webhook, WebhookDto>

    suspend fun updateWebhook(id: String, webhook: Webhook): EntityWithDto<Webhook, WebhookDto>

    suspend fun deleteWebhook(id: String)

    suspend fun isAvailable(nonce: String): Boolean

    suspend fun checkWebhookAvailability(webhook: Webhook)

    suspend fun subscribe(webhook: Webhook, chat: Chat)

    suspend fun unsubscribe(webhook: Webhook, chat: Chat)

    suspend fun isSubscribed(webhook: Webhook, chat: Chat): Boolean

    suspend fun hasAccess(chat: Chat, to: Webhook): Boolean

    suspend fun regenerateNonce(id: String): WebhookNonce
}

@Injectable
class WebhookServiceImpl : WebhookService {

    private val log = logging()

    private val webhookRepository by injecting<WebhookRepository>()

    private val webhookTargetRepository by injecting<WebhookTargetRepository>()

    private val webhookDtoConverter by injecting<DtoConverter<WebhookDto, Webhook>>()

    private val targetService by injecting<TargetService>()

    private val telegramBotFactory by injecting<TelegramBotFactory>()

    override suspend fun createWebhook(webhook: Webhook): EntityWithDto<Webhook, WebhookDto> {
        log.d {
            "Creating webhook $webhook"
        }

        webhook.nonce = createNonce().nonce

        telegramBotFactory.getTelegramBot(webhook)

        handlePsqlUniqueViolationOrThrow {
            webhookRepository.add(webhook)
        }

        val dto = webhookDtoConverter.convertToDto(webhook)

        return webhook to dto
    }

    override suspend fun getWebhook(id: String, requireDto: Boolean): EntityWithDto<Webhook, WebhookDto> {
        val webhook = webhookRepository.findWebhookByIdOrNameOrBotTokenOrNonce(id) ?: throw EntityNotFoundException()

        return webhook to webhookDtoConverter.convertToDtoIf(webhook, requireDto)
    }

    override suspend fun getWebhookTargetsByTarget(target: Target) =
        webhookTargetRepository.findWebhookTargetsByTargetId(target.id).map(WebhookTarget::webhook)

    override suspend fun getWebhookByNonce(nonce: String, requireDto: Boolean): EntityWithDto<Webhook, WebhookDto> {
        val webhook = webhookRepository.findWebhookByNonce(nonce) ?: throw EntityNotFoundException()

        return webhook to webhookDtoConverter.convertToDtoIf(webhook, requireDto)
    }

    override suspend fun getWebhooks(
        limit: Int?,
        offset: Int?,
        requireDto: Boolean
    ): ExportedSequenceWithDto<Webhook, WebhookDto> {
        val webhooks = webhookRepository.findAllWebhooks(limit ?: DEFAULT_LIMIT, offset ?: DEFAULT_OFFSET)

        return webhooks to webhookDtoConverter.convertElementsToDtoIf(webhooks, requireDto)
    }

    override suspend fun updateWebhook(id: String, webhook: Webhook): EntityWithDto<Webhook, WebhookDto> {
        log.d {
            "Updating webhook $id with data $webhook"
        }

        val (existingWebhook, _) = this.getWebhook(id)

        existingWebhook.apply {
            this.name = webhook.name
            this.botToken = webhook.botToken
            this.private = webhook.private
            this.state = webhook.state
            this.targetType = webhook.targetType
        }

        handlePsqlUniqueViolationOrThrow {
            existingWebhook.flushChanges()
        }

        return existingWebhook to webhookDtoConverter.convertToDto(existingWebhook)
    }

    override suspend fun deleteWebhook(id: String) {
        log.d {
            "Deleting webhook $id"
        }

        val (webhook, _) = this.getWebhook(id)

        telegramBotFactory.stopTelegramBot(webhook)

        webhook.delete()
    }

    override suspend fun isAvailable(nonce: String): Boolean {
        val (webhook, _) = this.getWebhookByNonce(nonce)

        return webhook.isAvailable
    }

    override suspend fun checkWebhookAvailability(webhook: Webhook) {
        if (!webhook.isAvailable) {
            throw UnavailableWebhookException()
        }
    }

    override suspend fun subscribe(webhook: Webhook, chat: Chat) {
        log.d {
            "Subscribing ${chat.id} to webhook $webhook."
        }

        this.checkWebhookAvailability(webhook)

        if (webhook.isTargetAcquired) {
            error("No more targets allowed for webhook $webhook with CHANNEL target type. Only one target can be registered.")
        }

        webhookTargetRepository.add(
            WebhookTarget {
                this.webhook = webhook
                this.target = targetService.createOrGetTargetForChat(chat)
            }
        )


        if (webhook.isAwaitingTarget) {
            webhook.state = WebhookState.AVAILABLE

            webhook.flushChanges()
        }

        log.d {
            "Successfully subscribed the chat to webhook $webhook."
        }
    }

    override suspend fun unsubscribe(webhook: Webhook, chat: Chat) {
        log.d {
            "Unsubscribing $chat from webhook $webhook."
        }

        val (target, _) = targetService.getTarget(chat.idString)

        webhookTargetRepository.deleteWebhookTarget(webhook, target) ?: throw EntityNotFoundException()

        log.d {
            "Successfully unsubscribed the chat from webhook $webhook."
        }
    }

    override suspend fun isSubscribed(webhook: Webhook, chat: Chat): Boolean =
        webhookTargetRepository.findWebhookTargetsByWebhookId(webhook.id).find {
            it.target.chatId == chat.idString
        } != null

    override suspend fun hasAccess(chat: Chat, to: Webhook): Boolean =
        !to.private || this.isSubscribed(to, chat)

    override suspend fun regenerateNonce(id: String): WebhookNonce {
        log.d {
            "Regenerating nonce for webhook $id"
        }

        val (webhook, _) = this.getWebhook(id)

        val generatedNonce = this.createNonce()

        webhook.nonce = generatedNonce.nonce

        webhook.flushChanges()

        return generatedNonce
    }

    private fun createNonce(): WebhookNonce {
        val buffer = ByteArray(BUFFER_SIZE)

        ThreadLocalRandom.current().nextBytes(buffer)

        return WebhookNonce(buffer.hex)
    }

    private companion object {

        private const val BUFFER_SIZE = 16
    }
}