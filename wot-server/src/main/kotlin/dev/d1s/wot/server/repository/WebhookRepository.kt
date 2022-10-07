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
import dev.d1s.teabag.ktorm.ExportedSequence
import dev.d1s.teabag.ktorm.export
import dev.d1s.teabag.ktorm.util.initValues
import dev.d1s.wot.server.entity.Webhook
import dev.d1s.wot.server.util.webhooks
import dispatch.core.withIO
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.dsl.or
import org.ktorm.entity.add
import org.ktorm.entity.find

interface WebhookRepository {

    suspend fun findWebhookByIdOrNameOrBotTokenOrNonce(id: String): Webhook?

    suspend fun findWebhookByNonce(nonce: String): Webhook?

    suspend fun findAllWebhooks(limit: Int, offset: Int): ExportedSequence<Webhook>

    suspend fun add(webhook: Webhook): Webhook
}

@Injectable
class WebhookRepositoryImpl : WebhookRepository {

    private val database by injecting<Database>()

    override suspend fun findWebhookByIdOrNameOrBotTokenOrNonce(id: String) = withIO {
        database.webhooks.find {
            (it.id eq id) or (it.name eq id) or (it.botToken eq id) or (it.nonce eq id)
        }
    }

    override suspend fun findWebhookByNonce(nonce: String): Webhook? = withIO {
        database.webhooks.find {
            it.nonce eq nonce
        }
    }

    override suspend fun findAllWebhooks(limit: Int, offset: Int) = withIO {
        database.webhooks.export(limit, offset)
    }

    override suspend fun add(webhook: Webhook) = withIO {
        webhook.initValues()

        database.webhooks.add(webhook)

        webhook
    }
}