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

package dev.d1s.wot.server.service

import cc.popkorn.annotations.Injectable
import cc.popkorn.injecting
import dev.d1s.teabag.dto.DtoConverter
import dev.d1s.teabag.dto.EntityWithDto
import dev.d1s.teabag.dto.convertToDtoIf
import dev.d1s.teabag.dto.ktorm.ExportedSequenceWithDto
import dev.d1s.teabag.dto.ktorm.convertElementsToDtoIf
import dev.d1s.teabag.ktorm.DEFAULT_LIMIT
import dev.d1s.teabag.ktorm.DEFAULT_OFFSET
import dev.d1s.teabag.postgresql.handlePsqlUniqueViolation
import dev.d1s.teabag.postgresql.handlePsqlUniqueViolationOrThrow
import dev.d1s.teabag.stdlib.exception.EntityAlreadyExistsException
import dev.d1s.teabag.stdlib.exception.EntityNotFoundException
import dev.d1s.wot.commons.dto.target.TargetDto
import dev.d1s.wot.server.entity.Target
import dev.d1s.wot.server.entity.Webhook
import dev.d1s.wot.server.entity.WebhookTarget
import dev.d1s.wot.server.repository.TargetRepository
import dev.d1s.wot.server.repository.WebhookTargetRepository
import dev.d1s.wot.server.util.idString
import dev.inmo.tgbotapi.types.chat.Chat
import dispatch.core.withIO
import org.lighthousegames.logging.logging

interface TargetService {

    suspend fun createTarget(target: Target): EntityWithDto<Target, TargetDto>

    suspend fun createOrGetTargetForChat(chat: Chat): Target

    suspend fun getTarget(id: String, requireDto: Boolean = false): EntityWithDto<Target, TargetDto>

    suspend fun getTargetByChatId(
        chatId: String,
        requireDto: Boolean = false
    ): EntityWithDto<Target, TargetDto>

    suspend fun getTargets(
        limit: Int?,
        offset: Int?,
        requireDto: Boolean = false
    ): ExportedSequenceWithDto<Target, TargetDto>

    suspend fun getWebhookTargetsByWebhook(webhook: Webhook): List<Target>

    suspend fun updateTarget(
        id: String,
        target: Target
    ): EntityWithDto<Target, TargetDto>

    suspend fun deleteTarget(id: String)

    suspend fun makeTargetUnavailable(target: Target): Target
}

@Injectable
class TargetServiceImpl : TargetService {

    private val log = logging()

    private val targetRepository by injecting<TargetRepository>()

    private val webhookTargetRepository by injecting<WebhookTargetRepository>()

    private val targetDtoConverter: DtoConverter<TargetDto, Target> by injecting()

    override suspend fun createTarget(target: Target): EntityWithDto<Target, TargetDto> {
        log.d {
            "Creating target $target"
        }

        val savedTarget = handlePsqlUniqueViolationOrThrow {
            targetRepository.add(target)
        }

        log.d {
            "Created target $savedTarget"
        }

        val targetDto = targetDtoConverter.convertToDto(savedTarget)

        return savedTarget to targetDto
    }

    override suspend fun createOrGetTargetForChat(chat: Chat): Target =
        targetRepository.findTargetByChatId(chat.idString) ?: createTarget(
            Target {
                this.chatId = chatId
                this.available = true
            }
        ).first // entity

    override suspend fun getTarget(id: String, requireDto: Boolean) =
        findTargetOrThrow(requireDto) {
            targetRepository.findTargetByIdOrChatId(id)
        }

    override suspend fun getTargetByChatId(chatId: String, requireDto: Boolean) =
        findTargetOrThrow(requireDto) {
            targetRepository.findTargetByChatId(chatId)
        }

    override suspend fun getTargets(
        limit: Int?,
        offset: Int?,
        requireDto: Boolean
    ): ExportedSequenceWithDto<Target, TargetDto> {
        val targets = targetRepository.findAllTargets(limit ?: DEFAULT_LIMIT, offset ?: DEFAULT_OFFSET)

        return targets to targetDtoConverter.convertElementsToDtoIf(targets, requireDto)
    }


    override suspend fun getWebhookTargetsByWebhook(webhook: Webhook) =
        webhookTargetRepository.findWebhookTargetsByTargetId(webhook.id).map(WebhookTarget::target)

    override suspend fun updateTarget(id: String, target: Target): EntityWithDto<Target, TargetDto> {
        log.d {
            "Updating target $id with data $target"
        }

        val (existingTarget, _) = this.getTarget(id)

        existingTarget.apply {
            this.chatId = target.chatId
            this.available = target.available
        }

        handlePsqlUniqueViolation {
            existingTarget.flushChanges()
        }.getOrElse {
            throw EntityAlreadyExistsException()
        }

        return existingTarget to targetDtoConverter.convertToDto(existingTarget)
    }

    override suspend fun deleteTarget(id: String) {
        log.d {
            "Deleting target $id"
        }

        val (existingTarget, _) = this.getTarget(id)

        existingTarget.delete()
    }


    override suspend fun makeTargetUnavailable(target: Target) = withIO {
        log.d {
            "Making target $target unavailable."
        }

        target.apply {
            available = false
            flushChanges()
        }
    }

    private inline fun findTargetOrThrow(requireDto: Boolean, find: () -> Target?): EntityWithDto<Target, TargetDto> {
        val target = find() ?: throw EntityNotFoundException()

        return target to targetDtoConverter.convertToDtoIf(target, requireDto)
    }
}