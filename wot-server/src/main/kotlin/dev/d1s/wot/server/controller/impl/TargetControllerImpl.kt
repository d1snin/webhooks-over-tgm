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
import dev.d1s.wot.commons.dto.target.TargetDto
import dev.d1s.wot.commons.dto.target.TargetUpsertDto
import dev.d1s.wot.server.controller.TargetController
import dev.d1s.wot.server.entity.target.Target
import dev.d1s.wot.server.service.TargetService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.*
import org.springframework.web.bind.annotation.RestController

@RestController
class TargetControllerImpl : TargetController {

    @set:Autowired
    lateinit var targetService: TargetService

    @set:Autowired
    lateinit var targetUpsertDtoConverter: DtoConverter<TargetUpsertDto, Target>

    @set:Autowired
    lateinit var locationFactory: LocationFactory

    @Secured
    override fun postTarget(targetUpsertDto: TargetUpsertDto): ResponseEntity<TargetDto> {
        val (target, targetDto) = targetService.createTarget(
            targetUpsertDtoConverter.convertToEntity(targetUpsertDto)
        )

        requireNotNull(targetDto)

        return created(
            locationFactory.newLocation(target)
        ).body(targetDto)
    }

    @Secured
    override fun getTarget(id: String): ResponseEntity<TargetDto> {
        val (_, targetDto) = targetService.getTarget(id, true)

        return ok(targetDto)
    }

    @Secured
    override fun getTargets(): ResponseEntity<List<TargetDto>> {
        val (_, targetDtoList) = targetService.getAllTargets(true)

        return ok(targetDtoList)
    }

    @Secured
    override fun putTarget(id: String, targetUpsertDto: TargetUpsertDto): ResponseEntity<TargetDto> {
        val (_, targetDto) = targetService.updateTarget(
            id, targetUpsertDtoConverter.convertToEntity(targetUpsertDto)
        )

        return ok(targetDto)
    }

    @Secured
    override fun deleteTarget(id: String): ResponseEntity<Any> {
        targetService.deleteTarget(id)

        return noContent().build()
    }
}