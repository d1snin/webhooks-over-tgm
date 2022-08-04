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

package dev.d1s.wot.client.converter

import dev.d1s.teabag.dto.DtoConverter
import kotlinx.coroutines.*

internal abstract class AbstractDtoConverter<D : Any, E : Any> : DtoConverter<D, E> {

    protected val deferredPropertyFetchingScope = CoroutineScope(Dispatchers.IO)

    protected inline fun <R> deferred(crossinline block: suspend () -> R): Deferred<R> =
        deferredPropertyFetchingScope.async(start = CoroutineStart.LAZY) {
            block()
        }
}