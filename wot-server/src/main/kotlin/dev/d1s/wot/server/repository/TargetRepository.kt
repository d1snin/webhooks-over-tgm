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

package dev.d1s.wot.server.repository

import cc.popkorn.annotations.Injectable
import cc.popkorn.injecting
import dev.d1s.teabag.ktorm.ExportedSequence
import dev.d1s.teabag.ktorm.export
import dev.d1s.teabag.ktorm.util.initValues
import dev.d1s.wot.server.entity.Target
import dev.d1s.wot.server.util.targets
import dispatch.core.withIO
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.dsl.or
import org.ktorm.entity.add
import org.ktorm.entity.find

interface TargetRepository {

    suspend fun findTargetByIdOrChatId(id: String): Target?

    suspend fun findTargetByChatId(chatId: String): Target?

    suspend fun findAllTargets(limit: Int, offset: Int): ExportedSequence<Target>

    suspend fun add(target: Target): Target
}

@Injectable
class TargetRepositoryImpl : TargetRepository {

    private val database by injecting<Database>()

    override suspend fun findTargetByIdOrChatId(id: String) = withIO {
        database.targets.find {
            (it.id eq id) or (it.chatId eq id)
        }
    }

    override suspend fun findTargetByChatId(chatId: String): Target? = withIO {
        database.targets.find {
            it.chatId eq chatId
        }
    }

    override suspend fun findAllTargets(limit: Int, offset: Int) = withIO {
        database.targets.export(limit, offset)
    }

    override suspend fun add(target: Target) = withIO {
        target.initValues()

        database.targets.add(target)

        target
    }
}