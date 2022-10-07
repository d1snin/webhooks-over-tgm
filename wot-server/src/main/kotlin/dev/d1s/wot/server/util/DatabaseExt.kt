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

package dev.d1s.wot.server.util

import dev.d1s.wot.server.table.Deliveries
import dev.d1s.wot.server.table.Targets
import dev.d1s.wot.server.table.WebhookTargets
import dev.d1s.wot.server.table.Webhooks
import org.ktorm.database.Database
import org.ktorm.entity.sequenceOf

val Database.deliveries get() = this.sequenceOf(Deliveries)

val Database.targets get() = this.sequenceOf(Targets)

val Database.webhooks get() = this.sequenceOf(Webhooks)

val Database.webhookTargets get() = this.sequenceOf(WebhookTargets)