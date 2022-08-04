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

package dev.d1s.wot.client.api

import dev.d1s.wot.client.builder.regular
import dev.d1s.wot.client.builder.textSources
import dev.d1s.wot.client.entity.content.MutableTextSourceList
import dev.d1s.wot.client.entity.content.TextSourceList

public sealed interface ContentPublisher<C> {

    public suspend fun publish(textSource: TextSourceList): C

    public suspend fun publish(builder: MutableTextSourceList.() -> Unit): C = this.publish(
        textSources(builder)
    )

    public suspend fun publish(regularText: String): C = this.publish(textSources {
        regular(regularText)
    })
}