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
import dev.d1s.teabag.stdlib.exception.InvalidEntityException
import dev.d1s.wot.commons.const.*
import dev.d1s.wot.commons.dto.target.TargetUpsertDto
import dev.d1s.wot.server.entity.Target
import dev.d1s.wot.server.service.TargetService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

private val targetService by injecting<TargetService>()
private val targetUpsertDtoConverter by injecting<DtoConverter<TargetUpsertDto, Target>>()

fun Route.targetRoutes() {

    post(POST_TARGET_MAPPING) {
        val targetDto = call.receive<TargetUpsertDto>()

        val target = targetUpsertDtoConverter.convertToEntity(targetDto)

        val (_, createdTarget) = targetService.createTarget(target)

        call.respond(HttpStatusCode.Created, createdTarget!!)
    }

    get(GET_TARGET_MAPPING) {
        val requestedId = call.parameters.id ?: throw InvalidEntityException()

        val (_, target) = targetService.getTarget(requestedId, true)

        call.respond(target!!)
    }

    get(GET_TARGETS_MAPPING) {
        val (limit, offset) = call.request.queryParameters.limitAndOffset

        val (_, deliveries) = targetService.getTargets(limit, offset, true)

        call.respond(deliveries!!)
    }

    put(PUT_TARGET_MAPPING) {
        val requestedId = call.parameters.id ?: throw InvalidEntityException()

        val targetDto = call.receive<TargetUpsertDto>()

        val target = targetUpsertDtoConverter.convertToEntity(targetDto)

        val (_, updatedTarget) = targetService.updateTarget(requestedId, target)

        call.respond(updatedTarget!!)
    }

    delete(DELETE_TARGET_MAPPING) {
        val requestedId = call.parameters.id ?: throw InvalidEntityException()

        targetService.deleteTarget(requestedId)

        call.respond(HttpStatusCode.NoContent)
    }
}