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
import dev.d1s.teabag.ktorm.findByIdFrom
import dev.d1s.teabag.ktorm.util.initValues
import dev.d1s.wot.server.entity.Delivery
import dev.d1s.wot.server.table.Deliveries
import dev.d1s.wot.server.util.deliveries
import dispatch.core.withIO
import org.ktorm.database.Database
import org.ktorm.entity.add

interface DeliveryRepository {

    suspend fun findDeliveryById(id: String): Delivery?

    suspend fun findAllDeliveries(limit: Int, offset: Int): ExportedSequence<Delivery>

    suspend fun add(delivery: Delivery): Delivery
}

@Injectable
class DeliveryRepositoryImpl : DeliveryRepository {

    private val database by injecting<Database>()

    override suspend fun findDeliveryById(id: String) = withIO {
        database.findByIdFrom(Deliveries, id)
    }

    override suspend fun findAllDeliveries(limit: Int, offset: Int) = withIO {
        database.deliveries.export(limit, offset)
    }

    override suspend fun add(delivery: Delivery) = withIO {
        delivery.initValues()

        database.deliveries.add(delivery)

        delivery
    }
}