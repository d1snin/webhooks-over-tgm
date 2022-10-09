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
import dev.d1s.wot.commons.dto.delivery.DeliveryCreationDto
import dev.d1s.wot.commons.dto.delivery.PublicDeliveryCreationDto
import dev.d1s.wot.server.entity.Delivery
import dev.d1s.wot.server.service.DeliveryService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

private val deliveryService by injecting<DeliveryService>()

private val deliveryCreationDtoConverter by injecting<DtoConverter<DeliveryCreationDto, Delivery>>()
private val publicDeliveryCreationDtoConverter by injecting<DtoConverter<PublicDeliveryCreationDto, Delivery>>()

fun Route.protectedDeliveryRoutes() {

    post(POST_DELIVERY_MAPPING) {
        val deliveryDto = call.receive<DeliveryCreationDto>()

        val delivery = deliveryCreationDtoConverter.convertToEntity(deliveryDto)

        call.respondWithCreatedDelivery(delivery)
    }

    get(GET_DELIVERY_MAPPING) {
        val requestedId = call.parameters.id.orInvalid()

        val (_, delivery) = deliveryService.getDeliveryById(requestedId, true)

        call.respond(delivery!!)
    }

    get(GET_DELIVERIES_MAPPING) {
        val (limit, offset) = call.request.queryParameters.limitAndOffset

        val (_, deliveries) = deliveryService.getDeliveries(limit, offset, true)

        call.respond(deliveries!!)
    }

    delete(DELETE_DELIVERY_MAPPING) {
        val requestedId = call.parameters.id.orInvalid()

        deliveryService.deleteDelivery(requestedId)

        call.respond(HttpStatusCode.NoContent)
    }
}

fun Route.unprotectedDeliveryRoutes() {

    post(POST_DELIVERY_PUBLIC_MAPPING) {
        val deliveryDto = call.receive<PublicDeliveryCreationDto>()

        val delivery = publicDeliveryCreationDtoConverter.convertToEntity(deliveryDto)

        call.respondWithCreatedDelivery(delivery)
    }
}

private suspend fun ApplicationCall.respondWithCreatedDelivery(delivery: Delivery) {
    val (_, createdDelivery) = deliveryService.createDelivery(delivery)

    this.respond(createdDelivery!!)
}