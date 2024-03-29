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

import dev.d1s.wot.server.constant.START_COMMAND
import dev.d1s.wot.server.constant.SUBSCRIBE_COMMAND
import dev.d1s.wot.server.constant.UNSUBSCRIBE_COMMAND
import dev.d1s.wot.server.entity.Webhook
import dev.inmo.tgbotapi.extensions.api.send.sendMessage
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onCommand
import dev.inmo.tgbotapi.extensions.utils.formatting.botCommand
import dev.inmo.tgbotapi.extensions.utils.formatting.buildEntities
import org.lighthousegames.logging.logging

private val log = logging()

suspend fun BehaviourContext.configureStartCommand(associatedWebhook: Webhook) {
    onCommand(START_COMMAND) {
        log.d {
            "Handled $START_COMMAND. Webhook: $associatedWebhook."
        }

        sendMessage(
            it.chat,
            buildEntities {
                +"Hi, here you can subscribe or unsubscribe from messages. Send me " +
                        botCommand(SUBSCRIBE_COMMAND) +
                        " or " +
                        botCommand(UNSUBSCRIBE_COMMAND) +
                        "."
            })
    }
}