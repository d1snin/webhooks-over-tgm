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
import dev.d1s.wot.commons.entity.target.TargetType
import dev.d1s.wot.commons.entity.webhook.WebhookState
import dev.d1s.wot.server.entity.Webhook
import org.ktorm.schema.boolean
import org.ktorm.schema.enum
import org.ktorm.schema.text

object Webhooks : Identifiables<Webhook>("webhook") {

    val name = text("name").bindTo {
        it.name
    }

    val botToken = text("bot_token").bindTo {
        it.botToken
    }

    val private = boolean("private").bindTo {
        it.private
    }

    val state = enum<WebhookState>("state").bindTo {
        it.state
    }

    val targetType = enum<TargetType>("target_type").bindTo {
        it.targetType
    }

    val nonce = text("nonce").bindTo {
        it.nonce
    }
}