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

package dev.d1s.wot.server.factory.impl

import dev.d1s.advice.entity.ErrorResponseData
import dev.d1s.advice.exception.HttpStatusException
import dev.d1s.wot.server.configurer.TelegramBotConfigurer
import dev.d1s.wot.server.constant.TELEGRAM_BOT_CACHE
import dev.d1s.wot.server.entity.bot.StartedTelegramBot
import dev.d1s.wot.server.entity.webhook.Webhook
import dev.d1s.wot.server.factory.TelegramBotFactory
import dev.inmo.tgbotapi.bot.exceptions.RequestException
import dev.inmo.tgbotapi.extensions.api.telegramBot
import kotlinx.coroutines.runBlocking
import org.lighthousegames.logging.logging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.Cacheable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component

@Component
class TelegramBotFactoryImpl : TelegramBotFactory {

    @set:Autowired
    lateinit var telegramBotConfigurer: TelegramBotConfigurer

    @set:Autowired
    lateinit var cacheManager: CacheManager

    private val botCache by lazy {
        cacheManager.getCache(TELEGRAM_BOT_CACHE)
    }

    private val log = logging()

    @Cacheable(TELEGRAM_BOT_CACHE, key = "#webhook.nonce")
    override fun getTelegramBot(webhook: Webhook): StartedTelegramBot {
        log.d {
            "Creating TelegramBot for webhook $webhook."
        }

        val bot = telegramBot(webhook.botToken)

        val job = runBlocking {
            try {
                telegramBotConfigurer.configure(bot, webhook)
            } catch (e: RequestException) {
                throw HttpStatusException(
                    ErrorResponseData(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "An error occurred while trying to configuring the bot: ${e.response.description ?: "no description"}"
                    )
                )
            }
        }

        log.d {
            "Created and started TelegramBot for webhook $webhook."
        }

        return StartedTelegramBot(bot, job)
    }

    override fun dememoizeAndStopTelegramBot(webhook: Webhook) {
        log.d {
            "Dememoizing and stopping TelegramBot for webhook $webhook."
        }

        val startedBot = botCache?.get(
            requireNotNull(webhook.nonce)
        )?.get() as? StartedTelegramBot?

        startedBot?.job?.cancel()
        startedBot?.bot?.close()
    }
}