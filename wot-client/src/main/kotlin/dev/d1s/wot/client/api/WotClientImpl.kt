package dev.d1s.wot.client.api

import dev.d1s.lp.client.factory.longPollingClient
import dev.d1s.teabag.stdlib.text.replaceIdPlaceholder
import dev.d1s.wot.client.entity.content.TextSourceList
import dev.d1s.wot.client.entity.delivery.Delivery
import dev.d1s.wot.client.entity.target.Target
import dev.d1s.wot.client.entity.webhook.Webhook
import dev.d1s.wot.client.entity.webhook.WebhookNonce
import dev.d1s.wot.client.factory.deliveryDtoConverter
import dev.d1s.wot.client.factory.httpClient
import dev.d1s.wot.client.factory.targetDtoConverter
import dev.d1s.wot.client.factory.webhookDtoConverter
import dev.d1s.wot.client.util.runHandling
import dev.d1s.wot.client.util.toTextSourceList
import dev.d1s.wot.client.util.validate
import dev.d1s.wot.client.util.validateTextSources
import dev.d1s.wot.commons.constant.*
import dev.d1s.wot.commons.dto.delivery.DeliveryCreationDto
import dev.d1s.wot.commons.dto.delivery.DeliveryDto
import dev.d1s.wot.commons.dto.target.TargetDto
import dev.d1s.wot.commons.dto.target.TargetUpsertDto
import dev.d1s.wot.commons.dto.webhook.WebhookDto
import dev.d1s.wot.commons.dto.webhook.WebhookUpsertDto
import dev.d1s.wot.commons.entity.content.Content
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

internal class WotClientImpl(

    override val baseUrl: String,

    override val authorization: String?,

    longPollingRecipient: String?

) : WotClient {

    override val longPollingClient = longPollingClient {
        recipient = longPollingRecipient
        baseUrl = this@WotClientImpl.baseUrl
        authorization = this@WotClientImpl.authorization
    }

    override val longPollingRecipient: String = longPollingRecipient ?: DEFAULT_LP_RECIPIENT

    private val httpClient = httpClient(this, authorization)

    private val deliveryDtoConverter = deliveryDtoConverter(this)
    private val targetDtoConverter = targetDtoConverter(this)
    private val webhookDtoConverter = webhookDtoConverter(this)

    override suspend fun postDelivery(webhookId: String, textSources: TextSourceList): Result<Delivery> = runHandling {
        val dto = httpClient.post(POST_DELIVERY_MAPPING) {
            contentType(ContentType.Application.Json)
            setBody(
                DeliveryCreationDto(
                    Content(
                        textSources.validateTextSources()
                    ), webhookId
                ).validate()
            )
        }.body<DeliveryDto>()

        deliveryDtoConverter.convertToEntity(dto)
    }

    override suspend fun postDelivery(webhook: Webhook, textSources: TextSourceList): Result<Delivery> =
        this.postDelivery(webhook.id, textSources)

    override suspend fun postDelivery(webhookId: String, regularText: String): Result<Delivery> = this.postDelivery(
        webhookId, regularText.toTextSourceList()
    )

    override suspend fun postDelivery(webhook: Webhook, regularText: String): Result<Delivery> = this.postDelivery(
        webhook.id, regularText.toTextSourceList()
    )

    override suspend fun getDelivery(id: String): Result<Delivery> = runHandling {
        val dto = httpClient.get(
            GET_DELIVERY_MAPPING.replaceIdPlaceholder(id)
        ).body<DeliveryDto>()

        deliveryDtoConverter.convertToEntity(dto)
    }

    override suspend fun getAllDeliveries(): Result<List<Delivery>> = runHandling {
        val dto = httpClient.get(GET_DELIVERIES_MAPPING).body<List<DeliveryDto>>()

        deliveryDtoConverter.convertToEntityList(dto)
    }

    override suspend fun deleteDelivery(id: String): Result<Unit> = runHandling {
        httpClient.delete(
            DELETE_DELIVERY_MAPPING.replaceIdPlaceholder(id)
        )
    }

    override suspend fun deleteDelivery(delivery: Delivery): Result<Unit> = this.deleteDelivery(delivery.id)

    override suspend fun postTarget(chatId: String, available: Boolean): Result<Target> = runHandling {
        val dto = httpClient.post(POST_TARGET_MAPPING) {
            contentType(ContentType.Application.Json)
            setBody(
                TargetUpsertDto(chatId, available).validate()
            )
        }.body<TargetDto>()

        targetDtoConverter.convertToEntity(dto)
    }

    override suspend fun getTarget(id: String): Result<Target> = runHandling {
        val dto = httpClient.get(
            GET_TARGET_MAPPING.replaceIdPlaceholder(id)
        ).body<TargetDto>()

        targetDtoConverter.convertToEntity(dto)
    }

    override suspend fun getAllTargets(): Result<List<Target>> = runHandling {
        val dto = httpClient.get(GET_TARGETS_MAPPING).body<List<TargetDto>>()

        targetDtoConverter.convertToEntityList(dto)
    }

    override suspend fun putTarget(targetId: String, chatId: String, available: Boolean): Result<Target> = runHandling {
        val dto = httpClient.put(
            PUT_TARGET_MAPPING.replaceIdPlaceholder(targetId)
        ) {
            contentType(ContentType.Application.Json)
            setBody(
                TargetUpsertDto(chatId, available).validate()
            )
        }.body<TargetDto>()

        targetDtoConverter.convertToEntity(dto)
    }

    override suspend fun putTarget(target: Target, chatId: String, available: Boolean): Result<Target> =
        this.putTarget(target.id, chatId, available)

    override suspend fun deleteTarget(id: String): Result<Unit> = runHandling {
        httpClient.delete(
            DELETE_TARGET_MAPPING.replaceIdPlaceholder(id)
        )
    }

    override suspend fun deleteTarget(target: Target): Result<Unit> = this.deleteTarget(target.id)

    override suspend fun postWebhook(
        name: String, botToken: String, private: Boolean, available: Boolean, targetIds: List<String>
    ): Result<Webhook> = runHandling {
        val dto = httpClient.post(POST_WEBHOOK_MAPPING) {
            contentType(ContentType.Application.Json)
            setBody(
                WebhookUpsertDto(
                    name, botToken, private, available, targetIds
                ).validate()
            )
        }.body<WebhookDto>()

        webhookDtoConverter.convertToEntity(dto)
    }

    override suspend fun getWebhook(id: String): Result<Webhook> = runHandling {
        val dto = httpClient.get(
            GET_WEBHOOK_MAPPING.replaceIdPlaceholder(id)
        ).body<WebhookDto>()

        webhookDtoConverter.convertToEntity(dto)
    }

    override suspend fun getAllWebhooks(): Result<List<Webhook>> = runHandling {
        val dto = httpClient.get(GET_WEBHOOKS_MAPPING).body<List<WebhookDto>>()

        webhookDtoConverter.convertToEntityList(dto)
    }

    override suspend fun putWebhook(
        webhookId: String, name: String, botToken: String, private: Boolean, available: Boolean, targetIds: List<String>
    ): Result<Webhook> = runHandling {
        val dto = httpClient.put(
            PUT_WEBHOOK_MAPPING.replaceIdPlaceholder(webhookId)
        ) {
            contentType(ContentType.Application.Json)
            setBody(
                WebhookUpsertDto(
                    name, botToken, private, available, targetIds
                ).validate()
            )
        }.body<WebhookDto>()

        webhookDtoConverter.convertToEntity(dto)
    }

    override suspend fun putWebhook(
        webhook: Webhook, name: String, botToken: String, private: Boolean, available: Boolean, targetIds: List<String>
    ): Result<Webhook> = this.putWebhook(
        webhook.id, name, botToken, private, available, targetIds
    )

    override suspend fun regenerateWebhookNonce(id: String): Result<WebhookNonce> = runHandling {
        httpClient.patch(
            PATCH_WEBHOOK_NONCE_MAPPING.replaceIdPlaceholder(id)
        ).body()
    }

    override suspend fun regenerateWebhookNonce(webhook: Webhook): Result<WebhookNonce> =
        this.regenerateWebhookNonce(webhook.id)

    override suspend fun deleteWebhook(id: String): Result<Unit> = runHandling {
        httpClient.delete(
            DELETE_WEBHOOK_MAPPING.replaceIdPlaceholder(id)
        )
    }

    override suspend fun deleteWebhook(webhook: Webhook): Result<Unit> = this.deleteWebhook(webhook.id)

    private companion object {
        private const val DEFAULT_LP_RECIPIENT = "wot-client"
    }
}