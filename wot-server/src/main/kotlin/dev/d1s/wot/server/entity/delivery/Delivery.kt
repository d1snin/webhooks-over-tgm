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

package dev.d1s.wot.server.entity.delivery

import com.vladmihalcea.hibernate.type.json.JsonType
import dev.d1s.teabag.data.jpa.Identifiable
import dev.d1s.wot.commons.entity.content.Content
import dev.d1s.wot.server.entity.webhook.Webhook
import org.hibernate.annotations.Type
import org.hibernate.annotations.TypeDef
import java.time.Instant
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = "delivery")
@TypeDef(name = "json", typeClass = JsonType::class)
data class Delivery(

    @Type(type = "json")
    @Column(nullable = false, columnDefinition = "jsonb")
    val content: Content,

    @ManyToOne(optional = false)
    val webhook: Webhook

) : Identifiable() {

    @Column(nullable = false)
    var successful: Boolean? = null

    @Column(nullable = false)
    var time: Instant? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Delivery

        if (id != other.id) return false
        if (content != other.content) return false
        if (successful != other.successful) return false
        if (webhook != other.webhook) return false

        return true
    }

    override fun hashCode(): Int {
        var result = requireNotNull(id).hashCode()
        result = 31 * result + content.hashCode()
        result = 31 * result + successful.hashCode()
        result = 31 * result + webhook.hashCode()
        return result
    }

    override fun toString() = "Delivery(" +
        "id='$id', " +
        "content=$content, " +
        "successful=$successful, " +
        "time=$time, " +
        "webhook=$webhook)"
}
