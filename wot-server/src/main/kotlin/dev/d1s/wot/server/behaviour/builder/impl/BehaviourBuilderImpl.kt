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

package dev.d1s.wot.server.behaviour.builder.impl

import dev.d1s.wot.server.behaviour.Behaviour
import dev.d1s.wot.server.behaviour.builder.BehaviourBuilder
import dev.d1s.wot.server.entity.webhook.Webhook
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import org.lighthousegames.logging.logging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component

@Primary
@Component
class BehaviourBuilderImpl : BehaviourBuilder {

    @set:Autowired
    lateinit var behaviours: List<Behaviour>

    private val log = logging()

    override suspend fun BehaviourContext.buildBehaviour(webhook: Webhook) {
        behaviours.forEach {
            log.d {
                "Building behaviour ${it::class.simpleName} for webhook $webhook."
            }

            with(it) {
                buildBehaviour(webhook)
            }
        }
    }
}