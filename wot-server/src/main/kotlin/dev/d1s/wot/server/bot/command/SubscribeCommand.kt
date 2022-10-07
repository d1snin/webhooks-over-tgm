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

package dev.d1s.wot.server.bot.command

import cc.popkorn.injecting
import dev.d1s.wot.server.constant.SUBSCRIBE_COMMAND
import dev.d1s.wot.server.entity.Webhook
import dev.d1s.wot.server.service.WebhookService
import dev.d1s.wot.server.util.checkPrivateChat
import dev.d1s.wot.server.util.safe
import dev.inmo.tgbotapi.extensions.api.send.reply
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.behaviour_builder.expectations.waitTextMessage
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onCommand
import io.ktor.http.*
import kotlinx.coroutines.flow.first
import org.lighthousegames.logging.logging

private val webhookService by injecting<WebhookService>()

private val log = logging()

suspend fun BehaviourContext.configureSubscribeCommand(associatedWebhook: Webhook) {
    onCommand(SUBSCRIBE_COMMAND) { message ->
        log.d {
            "Handled $SUBSCRIBE_COMMAND. Webhook: $associatedWebhook."
        }

        val chat = message.chat

        if (!checkPrivateChat(message)) {
            return@onCommand
        }

        if (!webhookService.hasAccess(chat, associatedWebhook)) {
            reply(
                message,
                "This webhook is private. Please send me a nonce of the webhook. " +
                        "You can also send a webhook URL containing a nonce."
            )

            val textMessage = waitTextMessage().first()

            val content = textMessage.content.text

            val nonce = associatedWebhook.nonce

            if (
                content == nonce || try {
                    URLBuilder(content).pathSegments.firstOrNull()?.equals(nonce) == true
                } catch (_: Exception) {
                    false
                }
            ) {
                safe(message) {
                    webhookService.subscribe(associatedWebhook, chat).also {
                        reply(message, "You are successfully authorized and subscribed!")
                    }
                }
            } else {
                reply(textMessage, "Invalid nonce provided.")
            }
        } else {
            safe(message) {
                webhookService.subscribe(associatedWebhook, chat).also {
                    reply(message, "You are successfully subscribed!")
                }
            }
        }
    }
}