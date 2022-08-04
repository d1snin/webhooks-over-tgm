package dev.d1s.wot.client.lp

import dev.d1s.lp.client.api.LongPollingClient
import dev.d1s.lp.client.listener.LongPollingEventHandler
import dev.d1s.lp.commons.event.data.EntityUpdatedEventData
import dev.d1s.wot.client.entity.webhook.WebhookNonce
import dev.d1s.wot.commons.constant.*
import dev.d1s.wot.commons.dto.delivery.DeliveryDto
import dev.d1s.wot.commons.dto.target.TargetDto
import dev.d1s.wot.commons.dto.webhook.WebhookDto

public suspend fun LongPollingClient.onDeliveryCreated(
    principal: String? = null, handler: LongPollingEventHandler<DeliveryDto>
) {
    onEvent(DELIVERY_CREATED_GROUP, principal, handler)
}

public suspend fun LongPollingClient.onDeliveryDeleted(
    principal: String? = null, handler: LongPollingEventHandler<DeliveryDto>
) {
    onEvent(DELIVERY_DELETED_GROUP, principal, handler)
}

public suspend fun LongPollingClient.onTargetCreated(
    principal: String? = null, handler: LongPollingEventHandler<TargetDto>
) {
    onEvent(TARGET_CREATED_GROUP, principal, handler)
}

public suspend fun LongPollingClient.onTargetUpdated(
    principal: String? = null, handler: LongPollingEventHandler<EntityUpdatedEventData<TargetDto>>
) {
    onEvent(TARGET_UPDATED_GROUP, principal, handler)
}

public suspend fun LongPollingClient.onTargetDeleted(
    principal: String? = null, handler: LongPollingEventHandler<TargetDto>
) {
    onEvent(TARGET_DELETED_GROUP, principal, handler)
}

public suspend fun LongPollingClient.onWebhookCreated(
    principal: String? = null, handler: LongPollingEventHandler<WebhookDto>
) {
    onEvent(WEBHOOK_CREATED_GROUP, principal, handler)
}

public suspend fun LongPollingClient.onWebhookUpdated(
    principal: String? = null, handler: LongPollingEventHandler<EntityUpdatedEventData<WebhookDto>>
) {
    onEvent(WEBHOOK_UPDATED_GROUP, principal, handler)
}

public suspend fun LongPollingClient.onWebhookDeleted(
    principal: String? = null, handler: LongPollingEventHandler<WebhookDto>
) {
    onEvent(WEBHOOK_DELETED_GROUP, principal, handler)
}

public suspend fun LongPollingClient.onWebhookNonceRegenerated(
    principal: String? = null, handler: LongPollingEventHandler<WebhookNonce>
) {
    onEvent(WEBHOOK_NONCE_REGENERATED_GROUP, principal, handler)
}

public suspend fun LongPollingClient.onWebhookSubscribed(
    principal: String? = null, handler: LongPollingEventHandler<WebhookDto>
) {
    onEvent(WEBHOOK_SUBSCRIBED_GROUP, principal, handler)
}

public suspend fun LongPollingClient.onWebhookUnsubscribed(
    principal: String? = null, handler: LongPollingEventHandler<WebhookDto>
) {
    onEvent(WEBHOOK_UNSUBSCRIBED_GROUP, principal, handler)
}

