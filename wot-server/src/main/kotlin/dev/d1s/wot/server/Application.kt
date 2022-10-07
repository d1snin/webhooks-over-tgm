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

package dev.d1s.wot.server

import cc.popkorn.injecting
import com.typesafe.config.ConfigFactory
import dev.d1s.ktor.events.server.WsEventPublisher
import dev.d1s.ktor.events.server.sendWsEvent
import dev.d1s.wot.server.configuration.*
import dispatch.core.MainCoroutineScope
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.lighthousegames.logging.logging

private val logger = logging()

// todo
private val cs = MainCoroutineScope()
private val publisher by injecting<WsEventPublisher>()

fun main() {
    logger.i {
        "Starting Webhooks over Telegram server..."
    }

    embeddedServer(Netty, applicationEngineEnvironment {
        config = HoconApplicationConfig(ConfigFactory.load())

        module {
            configureContentNegotiation()
            configureDatabase()
            configureRouting()
            configureSecurity()
            configureStatusPages()
            configureWsEvents()
        }

        connector {
            port = config.port
        }

        log.debug("Sending random events...")

        cs.launch {
            while (true) {
                publisher.sendWsEvent("thing", "Hello, World!")
                delay(500)
            }
        }
    }).start(true)
}
