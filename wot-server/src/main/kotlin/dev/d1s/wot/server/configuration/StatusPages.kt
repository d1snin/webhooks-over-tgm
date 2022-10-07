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

package dev.d1s.wot.server.configuration

import dev.d1s.teabag.ktor.server.handleCommonExceptions
import dev.d1s.wot.server.exception.DeliverySendingFailedException
import dev.d1s.wot.server.exception.InvalidContentException
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

fun Application.configureStatusPages() {
    install(StatusPages) {
        handleCommonExceptions()

        exception<DeliverySendingFailedException> { call, _ ->
            call.respond(HttpStatusCode.InternalServerError)
        }

        exception<InvalidContentException> {call, _ ->
            call.respond(HttpStatusCode.BadRequest)
        }
    }
}