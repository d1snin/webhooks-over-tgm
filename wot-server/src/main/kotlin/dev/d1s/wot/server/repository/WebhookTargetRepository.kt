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

package dev.d1s.wot.server.repository

import cc.popkorn.annotations.Injectable
import cc.popkorn.injecting
import dev.d1s.wot.server.entity.Target
import dev.d1s.wot.server.entity.Webhook
import dev.d1s.wot.server.entity.WebhookTarget
import dev.d1s.wot.server.util.webhookTargets
import dispatch.core.withIO
import org.ktorm.database.Database
import org.ktorm.dsl.and
import org.ktorm.dsl.eq
import org.ktorm.entity.add
import org.ktorm.entity.filter
import org.ktorm.entity.find
import org.ktorm.entity.toList

interface WebhookTargetRepository {

    suspend fun findWebhookTargetsByWebhookId(id: String): List<WebhookTarget>

    suspend fun findWebhookTargetsByTargetId(id: String): List<WebhookTarget>

    suspend fun add(webhookTarget: WebhookTarget)

    suspend fun deleteWebhookTarget(webhook: Webhook, target: Target): WebhookTarget?
}

@Injectable
class WebhookTargetRepositoryImpl : WebhookTargetRepository {

    private val database by injecting<Database>()

    override suspend fun findWebhookTargetsByWebhookId(id: String) = withIO {
        database.webhookTargets.filter {
            it.webhookId eq id
        }.toList()
    }

    override suspend fun findWebhookTargetsByTargetId(id: String) = withIO {
        database.webhookTargets.filter {
            it.targetId eq id
        }.toList()
    }

    override suspend fun add(webhookTarget: WebhookTarget) {
        withIO {
            database.webhookTargets.add(webhookTarget)
        }
    }

    override suspend fun deleteWebhookTarget(webhook: Webhook, target: Target): WebhookTarget? =
        withIO {
            database.webhookTargets.find {
                (it.webhookId eq webhook.id) and (it.targetId eq target.id)
            }?.apply {
                delete()
            }
        }
}