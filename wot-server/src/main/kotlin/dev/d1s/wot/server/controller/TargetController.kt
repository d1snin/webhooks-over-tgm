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
import dev.d1s.wot.commons.dto.target.TargetDto
import dev.d1s.wot.commons.dto.target.TargetUpsertDto
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import javax.validation.Valid
import javax.validation.constraints.NotBlank

@Validated
interface TargetController {

    @PostMapping(POST_TARGET_MAPPING)
    fun postTarget(
        @RequestBody
        @Valid
        targetUpsertDto: TargetUpsertDto
    ): ResponseEntity<TargetDto>

    @GetMapping(GET_TARGET_MAPPING)
    fun getTarget(
        @PathVariable
        @NotBlank
        id: String
    ): ResponseEntity<TargetDto>

    @GetMapping(GET_TARGETS_MAPPING)
    fun getTargets(): ResponseEntity<List<TargetDto>>

    @PutMapping(PUT_TARGET_MAPPING)
    fun putTarget(
        @PathVariable
        @NotBlank
        id: String,
        @RequestBody
        @Valid
        targetUpsertDto: TargetUpsertDto
    ): ResponseEntity<TargetDto>

    @DeleteMapping(DELETE_TARGET_MAPPING)
    fun deleteTarget(
        @PathVariable
        @NotBlank
        id: String
    ): ResponseEntity<Any>
}