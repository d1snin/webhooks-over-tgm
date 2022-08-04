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

package dev.d1s.wot.server.converter.dto.delivery

import dev.d1s.teabag.dto.DtoConverter
import dev.d1s.wot.commons.dto.delivery.DeliveryCreationDto
import dev.d1s.wot.server.entity.delivery.Delivery
import dev.d1s.wot.server.service.WebhookService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class DeliveryCreationDtoConverter : DtoConverter<DeliveryCreationDto, Delivery> {

    @set:Autowired
    lateinit var webhookService: WebhookService

    override fun convertToEntity(dto: DeliveryCreationDto) = dto.run {
        val (webhookEntity, _) = webhookService.getWebhook(webhook)

        Delivery(content, webhookEntity)
    }
}