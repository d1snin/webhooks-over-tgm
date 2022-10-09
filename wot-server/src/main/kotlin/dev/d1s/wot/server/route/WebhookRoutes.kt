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

package dev.d1s.wot.server.route

import cc.popkorn.injecting
import dev.d1s.teabag.dto.DtoConverter
import dev.d1s.teabag.ktor.server.id
import dev.d1s.teabag.ktor.server.limitAndOffset
import dev.d1s.teabag.stdlib.checks.orInvalid
import dev.d1s.teabag.stdlib.exception.InvalidEntityException
import dev.d1s.wot.commons.const.*
import dev.d1s.wot.commons.dto.webhook.WebhookUpsertDto
import dev.d1s.wot.server.entity.Webhook
import dev.d1s.wot.server.service.WebhookService
import dev.d1s.wot.server.util.webhookNonce
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

private val webhookService by injecting<WebhookService>()
private val webhookUpsertDtoConverter by injecting<DtoConverter<WebhookUpsertDto, Webhook>>()

fun Route.webhookRoutes() {

    post(POST_WEBHOOK_MAPPING) {
        val webhookDto = call.receive<WebhookUpsertDto>()

        val webhook = webhookUpsertDtoConverter.convertToEntity(webhookDto)

        val (_, createdWebhook) = webhookService.createWebhook(webhook)

        call.respond(HttpStatusCode.Created, createdWebhook!!)
    }

    get(GET_WEBHOOK_MAPPING) {
        val requestedId = call.parameters.id.orInvalid()

        val (_, webhook) = webhookService.getWebhook(requestedId, true)

        call.respond(webhook!!)
    }

    get(GET_WEBHOOK_AVAILABILITY_MAPPING) {
        val requestedNonce = call.parameters.webhookNonce.orInvalid()

        call.respond(
            if (webhookService.isAvailable(requestedNonce)) {
                HttpStatusCode.OK
            } else {
                HttpStatusCode.Forbidden
            }
        )
    }

    get(GET_WEBHOOKS_MAPPING) {
        val (limit, offset) = call.request.queryParameters.limitAndOffset

        val (_, webhooks) = webhookService.getWebhooks(limit, offset, true)

        call.respond(webhooks!!)
    }

    put(PUT_WEBHOOK_MAPPING) {
        val requestedId = call.parameters.id.orInvalid()

        val webhookDto = call.receive<WebhookUpsertDto>()

        val webhook = webhookUpsertDtoConverter.convertToEntity(webhookDto)

        val (_, updatedWebhook) = webhookService.updateWebhook(requestedId, webhook)

        call.respond(updatedWebhook!!)
    }

    patch(PATCH_WEBHOOK_NONCE_MAPPING) {
        val requestedId = call.parameters.id.orInvalid()

        val updatedNonce = webhookService.regenerateNonce(requestedId)

        call.respond(updatedNonce)
    }

    delete(DELETE_WEBHOOK_MAPPING) {
        val requestedId = call.parameters.id.orInvalid()

        webhookService.deleteWebhook(requestedId)

        call.respond(HttpStatusCode.NoContent)
    }
}