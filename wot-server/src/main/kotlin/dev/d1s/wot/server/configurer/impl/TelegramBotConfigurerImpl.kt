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

package dev.d1s.wot.server.configurer.impl

import dev.d1s.wot.commons.constant.*
import dev.d1s.wot.server.behaviour.builder.BehaviourBuilder
import dev.d1s.wot.server.configurer.TelegramBotConfigurer
import dev.d1s.wot.server.entity.webhook.Webhook
import dev.inmo.tgbotapi.bot.TelegramBot
import dev.inmo.tgbotapi.extensions.api.bot.setMyCommands
import dev.inmo.tgbotapi.extensions.behaviour_builder.buildBehaviourWithLongPolling
import dev.inmo.tgbotapi.types.BotCommand
import kotlinx.coroutines.Job
import org.lighthousegames.logging.logging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class TelegramBotConfigurerImpl : TelegramBotConfigurer {

    @set:Autowired
    lateinit var behaviourBuilder: BehaviourBuilder

    private val log = logging()

    override suspend fun configure(telegramBot: TelegramBot, webhook: Webhook): Job {
        log.d {
            "Configuring the bot for webhook $webhook."
        }

        telegramBot.setMyCommands(
            BotCommand(
                START_COMMAND,
                START_COMMAND_DESCRIPTION
            ),
            BotCommand(
                SUBSCRIBE_COMMAND,
                SUBSCRIBE_COMMAND_DESCRIPTION,
            ),
            BotCommand(
                UNSUBSCRIBE_COMMAND,
                UNSUBSCRIBE_COMMAND_DESCRIPTION
            )
        )

        return telegramBot.buildBehaviourWithLongPolling {
            with(behaviourBuilder) {
                buildBehaviour(webhook)
            }
        }
    }
}