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

package dev.d1s.wot.client.api

import dev.d1s.teabag.stdlib.text.replacePlaceholder
import dev.d1s.wot.client.entity.content.TextSourceList
import dev.d1s.wot.client.factory.httpClient
import dev.d1s.wot.client.util.runHandling
import dev.d1s.wot.client.util.validate
import dev.d1s.wot.commons.constant.POST_DELIVERY_PUBLIC_MAPPING
import dev.d1s.wot.commons.dto.delivery.DeliveryDto
import dev.d1s.wot.commons.dto.delivery.PublicDeliveryCreationDto
import dev.d1s.wot.commons.entity.content.Content
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

internal class WotWebhookClientImpl(

    override val baseUrl: String,

    override val nonce: String

) : WotWebhookClient {

    private val httpClient = httpClient(this)

    private val url = POST_DELIVERY_PUBLIC_MAPPING.replacePlaceholder("webhookNonce" to nonce)

    override suspend fun publish(textSource: TextSourceList): Result<DeliveryDto> = runHandling {
        httpClient.post(url) {
            contentType(ContentType.Application.Json)
            setBody(
                PublicDeliveryCreationDto(
                    Content(textSource)
                ).validate()
            )
        }.body()
    }
}