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

package dev.d1s.wot.server.controller

import dev.d1s.wot.server.constant.*
import dev.d1s.wot.server.dto.webhook.WebhookDto
import dev.d1s.wot.server.dto.webhook.WebhookUpsertDto
import dev.d1s.wot.server.entity.webhook.WebhookNonce
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import javax.validation.Valid
import javax.validation.constraints.NotBlank

@Validated
interface WebhookController {

    @PostMapping(POST_WEBHOOK_MAPPING)
    fun postWebhook(
        @RequestBody
        @Valid
        webhookUpsertDto: WebhookUpsertDto
    ): ResponseEntity<WebhookDto>

    @GetMapping(GET_WEBHOOK_MAPPING)
    fun getWebhook(
        @PathVariable
        @NotBlank
        id: String
    ): ResponseEntity<WebhookDto>

    @GetMapping(GET_WEBHOOKS_MAPPING)
    fun getWebhooks(): ResponseEntity<List<WebhookDto>>

    @GetMapping(GET_WEBHOOK_AVAILABILITY_MAPPING)
    fun getWebhookAvailability(
        @PathVariable
        @NotBlank
        webhookNonce: String
    ): ResponseEntity<Any>

    @PutMapping(PUT_WEBHOOK_MAPPING)
    fun putWebhook(
        @PathVariable
        @NotBlank
        id: String,
        @RequestBody
        @Valid
        webhookUpsertDto: WebhookUpsertDto
    ): ResponseEntity<WebhookDto>

    @PatchMapping(PATCH_WEBHOOK_NONCE_MAPPING)
    fun patchWebhookNonce(
        @PathVariable
        @NotBlank
        id: String
    ): ResponseEntity<WebhookNonce>

    @DeleteMapping(DELETE_WEBHOOK_MAPPING)
    fun deleteWebhook(
        @PathVariable
        @NotBlank
        id: String
    ): ResponseEntity<Any>
}