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

package dev.d1s.wot.server.controller

import dev.d1s.wot.commons.constant.*
import dev.d1s.wot.commons.dto.delivery.DeliveryCreationDto
import dev.d1s.wot.commons.dto.delivery.DeliveryDto
import dev.d1s.wot.commons.dto.delivery.PublicDeliveryCreationDto
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import javax.validation.Valid
import javax.validation.constraints.NotBlank

@Validated
interface DeliveryController {

    @PostMapping(POST_DELIVERY_MAPPING)
    fun postDelivery(
        @RequestBody
        @Valid
        deliveryCreationDto: DeliveryCreationDto
    ): ResponseEntity<DeliveryDto>

    @PostMapping(POST_DELIVERY_PUBLIC_MAPPING)
    fun postDeliveryFromPublic(
        @PathVariable
        @NotBlank
        webhookNonce: String,
        @RequestBody
        @Valid
        publicDeliveryCreationDto: PublicDeliveryCreationDto
    ): ResponseEntity<DeliveryDto>

    @GetMapping(GET_DELIVERY_MAPPING)
    fun getDelivery(
        @PathVariable
        @NotBlank
        id: String
    ): ResponseEntity<DeliveryDto>

    @GetMapping(GET_DELIVERIES_MAPPING)
    fun getDeliveries(): ResponseEntity<List<DeliveryDto>>

    @DeleteMapping(DELETE_DELIVERY_MAPPING)
    fun deleteDelivery(
        @PathVariable
        @NotBlank
        id: String
    ): ResponseEntity<Any>
}