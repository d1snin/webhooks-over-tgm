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

package dev.d1s.wot.server.entity.target

import dev.d1s.teabag.data.jpa.Identifiable
import dev.d1s.teabag.data.jpa.util.mapToIdList
import dev.d1s.wot.server.entity.webhook.Webhook
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.ManyToMany
import javax.persistence.Table

@Entity
@Table(name = "target")
data class Target(

    @Column(unique = true, nullable = false)
    var chatId: String,

    @Column(nullable = false)
    var available: Boolean

) : Identifiable() {

    @ManyToMany(mappedBy = "targets")
    var webhooks: List<Webhook> = listOf()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Target

        if (id != other.id) return false
        if (chatId != other.chatId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = requireNotNull(id).hashCode()
        result = 31 * result + chatId.hashCode()
        result = 31 * result + available.hashCode()
        return result
    }

    override fun toString() = "Target(" +
        "id='$id', " +
        "chatId='$chatId', " +
        "available=$available, " +
        "webhooks=${webhooks.mapToIdList()})"
}
