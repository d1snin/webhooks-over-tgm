package dev.d1s.wot.client.entity.webhook

import dev.d1s.wot.client.entity.target.Target
import kotlinx.coroutines.Deferred

public data class Webhook(
    val id: String,
    val name: String,
    val botToken: String,
    val private: Boolean,
    val targets: Deferred<List<Result<Target>>>
)
