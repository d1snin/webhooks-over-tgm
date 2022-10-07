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

package dev.d1s.wot.server.bot.configuration

import dev.d1s.wot.server.bot.command.configureStartCommand
import dev.d1s.wot.server.bot.command.configureSubscribeCommand
import dev.d1s.wot.server.bot.command.configureUnsubscribeCommand
import dev.d1s.wot.server.constant.*
import dev.inmo.tgbotapi.extensions.api.bot.setMyCommands
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.types.BotCommand

suspend fun BehaviourContext.configureCommands() {
    setMyCommands(
        BotCommand(START_COMMAND, START_COMMAND_DESCRIPTION),
        BotCommand(SUBSCRIBE_COMMAND, SUBSCRIBE_COMMAND_DESCRIPTION),
        BotCommand(UNSUBSCRIBE_COMMAND, UNSUBSCRIBE_COMMAND_DESCRIPTION)
    )

    configureStartCommand()
    configureSubscribeCommand()
    configureUnsubscribeCommand()
}