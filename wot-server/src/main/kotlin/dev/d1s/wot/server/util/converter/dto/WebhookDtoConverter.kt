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

package dev.d1s.wot.server.util.converter.dto

import cc.popkorn.annotations.Injectable
import dev.d1s.teabag.dto.ConverterMetadata
import dev.d1s.teabag.dto.DtoConverter
import dev.d1s.teabag.dto.getProperty
import dev.d1s.wot.commons.dto.webhook.WebhookDto
import dev.d1s.wot.server.entity.Webhook

@Injectable
class WebhookDtoConverter : DtoConverter<WebhookDto, Webhook> {

    override fun convertToDto(entity: Webhook, meta: ConverterMetadata) = entity.run {
        WebhookDto(
            id,
            createdAt,
            name,
            nonce,
            botToken,
            private,
            state,
            requireNotNull(meta.getProperty("webhook_target_ids")),
            requireNotNull(meta.getProperty("webhook_delivery_ids"))
        )
    }
}