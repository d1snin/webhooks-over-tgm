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

package dev.d1s.wot.server.util

import dev.inmo.tgbotapi.extensions.api.send.reply
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.utils.privateChatOrNull
import dev.inmo.tgbotapi.types.message.abstracts.CommonMessage
import dev.inmo.tgbotapi.types.message.content.TextContent
import dev.inmo.tgbotapi.utils.PreviewFeature

@OptIn(PreviewFeature::class)
suspend fun BehaviourContext.checkPrivateChat(
    message: CommonMessage<TextContent>
) = (message.chat.privateChatOrNull() != null).also {
    if (!it) {
        reply(message, "You can do this only in a private chat.")
    }
}

suspend fun BehaviourContext.safe(
    message: CommonMessage<TextContent>,
    block: suspend () -> Unit
) {
    try {
        block()
    } catch (t: Throwable) {
        t.printStackTrace()
        reply(message, "An error occurred.")
    }
}