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

package dev.d1s.wot.server.controller.impl

import dev.d1s.security.configuration.annotation.Secured
import dev.d1s.teabag.dto.DtoConverter
import dev.d1s.teabag.web.LocationFactory
import dev.d1s.wot.commons.dto.webhook.WebhookDto
import dev.d1s.wot.commons.dto.webhook.WebhookUpsertDto
import dev.d1s.wot.server.controller.WebhookController
import dev.d1s.wot.server.entity.webhook.Webhook
import dev.d1s.wot.server.entity.webhook.WebhookNonce
import dev.d1s.wot.server.service.WebhookService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.*
import org.springframework.web.bind.annotation.RestController

@RestController
class WebhookControllerImpl : WebhookController {

    @set:Autowired
    lateinit var webhookService: WebhookService

    @set:Autowired
    lateinit var webhookUpsertDtoConverter: DtoConverter<WebhookUpsertDto, Webhook>

    @set:Autowired
    lateinit var locationFactory: LocationFactory

    @Secured
    override fun postWebhook(webhookUpsertDto: WebhookUpsertDto): ResponseEntity<WebhookDto> {
        val (webhook, webhookDto) = webhookService.createWebhook(
            webhookUpsertDtoConverter.convertToEntity(
                webhookUpsertDto
            )
        )

        requireNotNull(webhookDto)

        return created(
            locationFactory.newLocation(webhook)
        ).body(webhookDto)
    }

    @Secured
    override fun getWebhook(id: String): ResponseEntity<WebhookDto> {
        val (_, webhookDto) = webhookService.getWebhook(id, true)

        return ok(webhookDto)
    }

    @Secured
    override fun getWebhooks(): ResponseEntity<List<WebhookDto>> {
        val (_, webhookDtoList) = webhookService.getAllWebhooks(true)

        return ok(webhookDtoList)
    }

    // not using @Secured here
    override fun getWebhookAvailability(webhookNonce: String): ResponseEntity<Any> =
        if (webhookService.isAvailable(webhookNonce)) {
            ok().build()
        } else {
            status(HttpStatus.FORBIDDEN).build()
        }

    @Secured
    override fun putWebhook(id: String, webhookUpsertDto: WebhookUpsertDto): ResponseEntity<WebhookDto> {
        val (_, webhookDto) = webhookService.updateWebhook(
            id,
            webhookUpsertDtoConverter.convertToEntity(webhookUpsertDto)
        )

        return ok(webhookDto)
    }

    @Secured
    override fun deleteWebhook(id: String): ResponseEntity<Any> {
        webhookService.deleteWebhook(id)

        return noContent().build()
    }

    @Secured
    override fun patchWebhookNonce(id: String): ResponseEntity<WebhookNonce> = ok(
        webhookService.regenerateNonce(id)
    )
}