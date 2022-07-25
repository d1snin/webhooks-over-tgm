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

package dev.d1s.wot.server.entity.webhook

import dev.d1s.teabag.data.jpa.Identifiable
import dev.d1s.teabag.data.jpa.util.mapToIdList
import dev.d1s.wot.server.entity.delivery.Delivery
import dev.d1s.wot.server.entity.target.Target
import javax.persistence.*

@Entity
@Table(name = "webhook")
data class Webhook(

    @Column(unique = true, nullable = false)
    var name: String,

    @Column(unique = true, nullable = false)
    var botToken: String,

    @Column(nullable = false)
    var private: Boolean,

    @ManyToMany(cascade = [CascadeType.PERSIST])
    @JoinTable(
        name = "webhook_target",
        joinColumns = [JoinColumn(name = "webhook_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "target_id", referencedColumnName = "id")]
    )
    var targets: MutableList<Target> = mutableListOf()

) : Identifiable() {

    @Column(unique = true, nullable = false)
    var nonce: String? = null

    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "webhook")
    val deliveries: List<Delivery> = listOf()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Webhook

        if (id != other.id) return false
        if (name != other.name) return false
        if (nonce != other.nonce) return false
        if (botToken != other.botToken) return false
        if (private != other.private) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + nonce.hashCode()
        result = 31 * result + botToken.hashCode()
        result = 31 * result + private.hashCode()
        return result
    }

    override fun toString() = "Webhook(" +
        "id='$id', " +
        "name='$name', " +
        "nonce='$nonce', " +
        "botToken='$botToken', " +
        "private=$private, " +
        "targets=${targets.mapToIdList()}, " +
        "deliveries=${deliveries.mapToIdList()})"
}
