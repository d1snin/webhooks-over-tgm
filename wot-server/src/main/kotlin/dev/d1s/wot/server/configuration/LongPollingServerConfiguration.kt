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

package dev.d1s.wot.server.configuration

import dev.d1s.lp.server.configurer.LongPollingServerConfigurer
import dev.d1s.wot.commons.constant.*
import org.springframework.context.annotation.Configuration

@Configuration
class LongPollingServerConfiguration : LongPollingServerConfigurer {

    override fun getAvailableGroups() = setOf(
        DELIVERY_CREATED_GROUP,
        DELIVERY_DELETED_GROUP,

        TARGET_CREATED_GROUP,
        TARGET_UPDATED_GROUP,
        TARGET_DELETED_GROUP,

        WEBHOOK_CREATED_GROUP,
        WEBHOOK_UPDATED_GROUP,
        WEBHOOK_DELETED_GROUP,
        WEBHOOK_NONCE_REGENERATED_GROUP,
        WEBHOOK_SUBSCRIBED_GROUP,
        WEBHOOK_UNSUBSCRIBED_GROUP
    )
}