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

package dev.d1s.wot.server.bot.factory

import dev.d1s.wot.server.bot.configuration.configureCommands
import dev.d1s.wot.server.entity.StartedTelegramBot
import dev.d1s.wot.server.entity.Webhook
import dev.d1s.wot.server.exception.TelegramBotStartFailedException
import dev.inmo.tgbotapi.bot.exceptions.RequestException
import dev.inmo.tgbotapi.bot.ktor.telegramBot
import dev.inmo.tgbotapi.extensions.behaviour_builder.buildBehaviourWithLongPolling
import kotlinx.coroutines.runBlocking
import org.lighthousegames.logging.logging
import java.util.concurrent.ConcurrentHashMap

private val log = logging()

private val telegramBotCache = ConcurrentHashMap<Webhook, StartedTelegramBot>()

fun getTelegramBot(webhook: Webhook): StartedTelegramBot {
    log.d {
        "Getting TelegramBot for webhook $webhook"
    }

    telegramBotCache[webhook]?.let {
        return it
    }

    val bot = telegramBot(webhook.botToken)

    return runBlocking {
        try {
            val job = bot.buildBehaviourWithLongPolling {
                configureCommands()
            }

            StartedTelegramBot(bot, job).also {
                telegramBotCache[webhook] = it

                log.d {
                    "Created and started TelegramBot for webhook $webhook"
                }
            }
        } catch (e: RequestException) {
            throw TelegramBotStartFailedException(e)
        }
    }
}

fun stopTelegramBot(webhook: Webhook) {
    log.d {
        "Dememoizing and stopping TelegramBot for webhook $webhook"
    }

    val startedBot = telegramBotCache[webhook] ?: error("No running bot for webhook $webhook")

    startedBot.run {
        bot.close()
        job.cancel()
    }
}