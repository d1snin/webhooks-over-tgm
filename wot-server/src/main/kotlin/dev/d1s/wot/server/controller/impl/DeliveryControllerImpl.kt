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

package dev.d1s.wot.server.controller.impl

import dev.d1s.security.configuration.annotation.Secured
import dev.d1s.teabag.dto.DtoConverter
import dev.d1s.teabag.web.LocationFactory
import dev.d1s.wot.commons.dto.delivery.DeliveryCreationDto
import dev.d1s.wot.commons.dto.delivery.DeliveryDto
import dev.d1s.wot.commons.dto.delivery.PublicDeliveryCreationDto
import dev.d1s.wot.server.controller.DeliveryController
import dev.d1s.wot.server.entity.delivery.Delivery
import dev.d1s.wot.server.service.DeliveryService
import dev.d1s.wot.server.service.WebhookService
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.*
import org.springframework.web.bind.annotation.RestController

@RestController
class DeliveryControllerImpl : DeliveryController {

    @set:Autowired
    lateinit var deliveryService: DeliveryService

    @set:Autowired
    lateinit var webhookService: WebhookService

    @set:Autowired
    lateinit var deliveryCreationDtoConverter: DtoConverter<DeliveryCreationDto, Delivery>

    @set:Autowired
    lateinit var locationFactory: LocationFactory

    @Secured
    override fun postDelivery(deliveryCreationDto: DeliveryCreationDto): ResponseEntity<DeliveryDto> {
        val (delivery, deliveryDto) = runBlocking {
            deliveryService.createDelivery(
                deliveryCreationDtoConverter.convertToEntity(deliveryCreationDto)
            )
        }

        requireNotNull(deliveryDto)

        return created(
            locationFactory.newLocation(delivery)
        ).body(deliveryDto)
    }

    // not placing @Secured here.
    override fun postDeliveryFromPublic(
        webhookNonce: String,
        publicDeliveryCreationDto: PublicDeliveryCreationDto
    ): ResponseEntity<DeliveryDto> {
        val (_, deliveryDto) = runBlocking {
            val (webhook, _) = webhookService.getWebhookByNonce(webhookNonce)

            deliveryService.createDelivery(
                Delivery(
                    publicDeliveryCreationDto.content,
                    webhook
                )
            )
        }

        return status(HttpStatus.CREATED).body(deliveryDto)
    }

    @Secured
    override fun getDelivery(id: String): ResponseEntity<DeliveryDto> {
        val (_, deliveryDto) = deliveryService.getDeliveryById(id, true)

        return ok(deliveryDto)
    }

    @Secured
    override fun getDeliveries(): ResponseEntity<List<DeliveryDto>> {
        val (_, deliveryDtoList) = deliveryService.getAllDeliveries(true)

        return ok(deliveryDtoList)
    }

    @Secured
    override fun deleteDelivery(id: String): ResponseEntity<Any> {
        deliveryService.deleteDelivery(id)

        return noContent().build()
    }
}