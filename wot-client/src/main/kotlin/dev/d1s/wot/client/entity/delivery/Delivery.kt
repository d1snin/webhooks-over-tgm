package dev.d1s.wot.client.entity.delivery

import dev.d1s.wot.client.entity.webhook.Webhook
import dev.d1s.wot.commons.entity.content.Content
import kotlinx.coroutines.Deferred
import java.time.Instant

public data class Delivery(
    val id: String,
    val content: Content,
    val successful: Boolean,
    val time: Instant,
    val webhook: Deferred<Result<Webhook>>
)
