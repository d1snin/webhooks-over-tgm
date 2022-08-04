package dev.d1s.wot.client.entity.target

import dev.d1s.wot.client.entity.webhook.Webhook
import kotlinx.coroutines.Deferred

public data class Target(
    val id: String,
    val chatId: String,
    val available: Boolean,
    val webhooks: Deferred<List<Result<Webhook>>>
)
