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

package dev.d1s.wot.client.factory

import dev.d1s.teabag.dto.DtoConverter
import dev.d1s.wot.client.api.WotClient
import dev.d1s.wot.client.converter.impl.DeliveryDtoConverter
import dev.d1s.wot.client.converter.impl.TargetDtoConverter
import dev.d1s.wot.client.converter.impl.WebhookDtoConverter
import dev.d1s.wot.client.entity.delivery.Delivery
import dev.d1s.wot.client.entity.target.Target
import dev.d1s.wot.client.entity.webhook.Webhook
import dev.d1s.wot.commons.dto.delivery.DeliveryDto
import dev.d1s.wot.commons.dto.target.TargetDto
import dev.d1s.wot.commons.dto.webhook.WebhookDto

internal fun deliveryDtoConverter(wotClient: WotClient): DtoConverter<DeliveryDto, Delivery> =
    DeliveryDtoConverter(wotClient)

internal fun targetDtoConverter(wotClient: WotClient): DtoConverter<TargetDto, Target> =
    TargetDtoConverter(wotClient)

internal fun webhookDtoConverter(wotClient: WotClient): DtoConverter<WebhookDto, Webhook> =
    WebhookDtoConverter(wotClient)