/*
 * Copyright 2022 Webhooks over Telegram project contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.d1s.wot.server.repository

import dev.d1s.wot.server.entity.webhook.Webhook
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface WebhookRepository : JpaRepository<Webhook, String> {

    fun findWebhookByName(name: String): Optional<Webhook>

    fun findWebhookByNonce(nonce: String): Optional<Webhook>

    fun findWebhookByBotToken(botToken: String): Optional<Webhook>

    @Query("select w from Webhook w where w.id = :id or w.name = :id or w.nonce = :id")
    fun findWebhookByIdOrNameOrNonce(@Param("id") id: String): Optional<Webhook>
}