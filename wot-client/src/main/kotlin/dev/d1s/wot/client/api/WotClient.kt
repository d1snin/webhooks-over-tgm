package dev.d1s.wot.client.api

import dev.d1s.lp.client.api.LongPollingClient
import dev.d1s.wot.client.entity.content.TextSourceList
import dev.d1s.wot.client.entity.delivery.Delivery
import dev.d1s.wot.client.entity.target.Target
import dev.d1s.wot.client.entity.webhook.Webhook
import dev.d1s.wot.client.entity.webhook.WebhookNonce

public sealed interface WotClient : BaseUrlAware {

    public val longPollingClient: LongPollingClient

    public val longPollingRecipient: String

    public val authorization: String?

    public suspend fun postDelivery(
        webhookId: String, textSources: TextSourceList
    ): Result<Delivery>

    public suspend fun postDelivery(
        webhook: Webhook, textSources: TextSourceList
    ): Result<Delivery>

    public suspend fun postDelivery(
        webhookId: String, regularText: String
    ): Result<Delivery>

    public suspend fun postDelivery(
        webhook: Webhook, regularText: String
    ): Result<Delivery>

    public suspend fun getDelivery(
        id: String
    ): Result<Delivery>

    public suspend fun getAllDeliveries(): Result<List<Delivery>>

    public suspend fun deleteDelivery(
        id: String
    ): Result<Unit>

    public suspend fun deleteDelivery(
        delivery: Delivery
    ): Result<Unit>

    public suspend fun postTarget(
        chatId: String, available: Boolean = true
    ): Result<Target>

    public suspend fun getTarget(
        id: String
    ): Result<Target>

    public suspend fun getAllTargets(): Result<List<Target>>

    public suspend fun putTarget(
        targetId: String, chatId: String, available: Boolean = true
    ): Result<Target>

    public suspend fun putTarget(
        target: Target, chatId: String, available: Boolean = true
    ): Result<Target>

    public suspend fun deleteTarget(
        id: String
    ): Result<Unit>

    public suspend fun deleteTarget(
        target: Target
    ): Result<Unit>

    public suspend fun postWebhook(
        name: String, botToken: String, private: Boolean, available: Boolean, targetIds: List<String>
    ): Result<Webhook>

    public suspend fun getWebhook(
        id: String
    ): Result<Webhook>

    public suspend fun getAllWebhooks(): Result<List<Webhook>>

    public suspend fun putWebhook(
        webhookId: String, name: String, botToken: String, private: Boolean, available: Boolean, targetIds: List<String>
    ): Result<Webhook>

    public suspend fun putWebhook(
        webhook: Webhook, name: String, botToken: String, private: Boolean, available: Boolean, targetIds: List<String>
    ): Result<Webhook>

    public suspend fun regenerateWebhookNonce(
        id: String
    ): Result<WebhookNonce>

    public suspend fun regenerateWebhookNonce(
        webhook: Webhook
    ): Result<WebhookNonce>

    public suspend fun deleteWebhook(
        id: String
    ): Result<Unit>

    public suspend fun deleteWebhook(
        webhook: Webhook
    ): Result<Unit>
}