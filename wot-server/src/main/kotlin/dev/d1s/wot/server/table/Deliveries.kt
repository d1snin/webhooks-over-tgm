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

package dev.d1s.wot.server.table

import dev.d1s.teabag.ktorm.table.Identifiables
import dev.d1s.wot.commons.entity.content.Content
import dev.d1s.wot.server.entity.Delivery
import org.ktorm.jackson.json
import org.ktorm.schema.boolean
import org.ktorm.schema.text

object Deliveries : Identifiables<Delivery>("delivery") {

    val content = json<Content>("content").bindTo {
        it.content
    }

    val webhook = text("webhook_id").references(Webhooks) {
        it.webhook
    }

    val successful = boolean("successful").bindTo {
        it.successful
    }
}