/*
 * Copyright 2022 Webhooks over Telegram project contributors
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

import dev.d1s.teabag.dto.EntityWithDto
import dev.d1s.teabag.dto.EntityWithDtoList
import dev.d1s.wot.commons.dto.webhook.WebhookDto
import dev.d1s.wot.server.entity.webhook.Webhook
import dev.d1s.wot.server.entity.webhook.WebhookNonce
import dev.inmo.tgbotapi.types.chat.Chat

interface WebhookService {

    fun createWebhook(webhook: Webhook): EntityWithDto<Webhook, WebhookDto>

    fun getWebhook(id: String, requireDto: Boolean = false): EntityWithDto<Webhook, WebhookDto>

    fun getWebhookByNonce(nonce: String, requireDto: Boolean = false): EntityWithDto<Webhook, WebhookDto>

    fun getAllWebhooks(requireDto: Boolean = false): EntityWithDtoList<Webhook, WebhookDto>

    fun updateWebhook(id: String, webhook: Webhook): EntityWithDto<Webhook, WebhookDto>

    fun deleteWebhook(id: String)

    fun isAvailable(id: String): Boolean

    fun checkAvailability(webhook: Webhook)

    fun subscribe(webhook: Webhook, chat: Chat)

    fun unsubscribe(webhook: Webhook, chat: Chat)

    fun isSubscribed(webhook: Webhook, chat: Chat): Boolean

    fun hasAccess(chat: Chat, to: Webhook): Boolean

    fun regenerateNonce(id: String): WebhookNonce

    fun checkForCollision(webhook: Webhook)
}