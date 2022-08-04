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

package dev.d1s.wot.client.starter.property

import dev.d1s.wot.client.starter.constant.WOT_WEBHOOK_CLUSTER_PREFIX
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.util.StringUtils
import javax.validation.constraints.NotBlank

@ConstructorBinding
@ConfigurationProperties(WOT_WEBHOOK_CLUSTER_PREFIX)
internal data class WotWebhookClientClusterConfigurationProperties(

    @field:NotBlank
    val webhookUrls: String
) {
    val parsedWebhookUrls = StringUtils.commaDelimitedListToSet(webhookUrls)
}