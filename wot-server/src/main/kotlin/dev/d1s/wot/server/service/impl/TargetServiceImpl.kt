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

package dev.d1s.wot.server.service.impl

import dev.d1s.advice.exception.UnprocessableEntityException
import dev.d1s.advice.util.orElseNotFound
import dev.d1s.lp.commons.event.data.EntityUpdatedEventData
import dev.d1s.lp.server.publisher.AsyncLongPollingEventPublisher
import dev.d1s.teabag.dto.DtoConverter
import dev.d1s.teabag.dto.EntityWithDto
import dev.d1s.teabag.dto.EntityWithDtoList
import dev.d1s.teabag.dto.util.convertToDtoIf
import dev.d1s.teabag.dto.util.convertToDtoListIf
import dev.d1s.wot.commons.constant.TARGET_CREATED_GROUP
import dev.d1s.wot.commons.constant.TARGET_DELETED_GROUP
import dev.d1s.wot.commons.constant.TARGET_UPDATED_GROUP
import dev.d1s.wot.commons.dto.target.TargetDto
import dev.d1s.wot.server.entity.target.Target
import dev.d1s.wot.server.repository.TargetRepository
import dev.d1s.wot.server.service.TargetService
import org.lighthousegames.logging.logging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TargetServiceImpl : TargetService {

    @set:Autowired
    lateinit var targetRepository: TargetRepository

    @set:Autowired
    lateinit var targetDtoConverter: DtoConverter<TargetDto, Target>

    @set:Autowired
    lateinit var publisher: AsyncLongPollingEventPublisher

    @set:Lazy
    @set:Autowired
    lateinit var targetServiceImpl: TargetServiceImpl

    val log = logging()

    @Transactional
    override fun createTarget(target: Target): EntityWithDto<Target, TargetDto> {
        log.d {
            "Creating target $target."
        }

        targetServiceImpl.checkForCollision(target.chatId)

        val savedTarget = targetRepository.save(target)

        log.d {
            "Successfully created target $savedTarget."
        }

        val targetDto = targetDtoConverter.convertToDto(savedTarget)

        publisher.publish(
            TARGET_CREATED_GROUP, requireNotNull(savedTarget.id), targetDto
        )

        return savedTarget to targetDto
    }

    @Transactional
    override fun createOrGetTargetForChar(chatId: String): Target = targetRepository.findTargetByChatId(chatId).orElse(
        targetRepository.save(
            Target(
                chatId,
                true,
            )
        )
    )

    @Transactional(readOnly = true)
    override fun getTarget(id: String, requireDto: Boolean): EntityWithDto<Target, TargetDto> {
        val target = targetRepository.findTargetByIdOrChatId(id).orElseNotFound(NOT_FOUND_MESSAGE)

        return target to targetDtoConverter.convertToDtoIf(target, requireDto)
    }

    @Transactional(readOnly = true)
    override fun getTargetByChatId(chatId: String, requireDto: Boolean): EntityWithDto<Target, TargetDto> {
        val target = targetRepository.findTargetByChatId(chatId).orElseNotFound(NOT_FOUND_MESSAGE)

        return target to targetDtoConverter.convertToDtoIf(target, requireDto)
    }

    @Transactional(readOnly = true)
    override fun getAllTargets(requireDto: Boolean): EntityWithDtoList<Target, TargetDto> {
        val targets = targetRepository.findAll()

        return targets to targetDtoConverter.convertToDtoListIf(targets, requireDto)
    }

    @Transactional
    override fun updateTarget(id: String, target: Target): EntityWithDto<Target, TargetDto> {
        log.d {
            "Updating target $id with new data $target."
        }

        val (existingTarget, existingTargetDto) = targetServiceImpl.getTarget(id, true)

        val chatId = target.chatId

        targetServiceImpl.checkForCollision(chatId)

        existingTarget.chatId = chatId
        existingTarget.available = target.available

        val savedTarget = targetRepository.save(existingTarget)

        log.d {
            "Successfully updated target $savedTarget."
        }

        val targetDto = targetDtoConverter.convertToDto(savedTarget)

        publisher.publish(
            TARGET_UPDATED_GROUP,
            requireNotNull(existingTarget.id),
            EntityUpdatedEventData(requireNotNull(existingTargetDto), targetDto)
        )

        return savedTarget to targetDto
    }

    @Transactional
    override fun deleteTarget(id: String) {
        log.d {
            "Deleting target $id."
        }

        val (existingTarget, targetDto) = targetServiceImpl.getTarget(id, true)

        targetRepository.delete(existingTarget)

        log.d {
            "Successfully deleted target $existingTarget."
        }

        publisher.publish(
            TARGET_DELETED_GROUP, requireNotNull(existingTarget.id), targetDto
        )
    }

    @Transactional
    override fun makeUnavailable(target: Target) {
        log.d {
            "Making target $target unavailable."
        }

        if (target.available) {
            target.available = false
            targetRepository.save(target)
        }

        log.d {
            "Made target $target unavailable."
        }
    }

    @Transactional(readOnly = true)
    override fun checkForCollision(chatId: String) {
        if (targetRepository.findTargetByChatId(chatId).isPresent) {
            throw UnprocessableEntityException("Target with the same chat id already exists.")
        }
    }

    private companion object {

        private const val NOT_FOUND_MESSAGE = "Target not found."
    }
}