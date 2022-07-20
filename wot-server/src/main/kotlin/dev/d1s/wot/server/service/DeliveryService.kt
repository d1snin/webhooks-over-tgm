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

package dev.d1s.wot.server.service

import dev.d1s.teabag.dto.EntityWithDto
import dev.d1s.teabag.dto.EntityWithDtoList
import dev.d1s.wot.server.dto.delivery.DeliveryDto
import dev.d1s.wot.server.entity.delivery.Delivery

interface DeliveryService {

    suspend fun createDelivery(delivery: Delivery): EntityWithDto<Delivery, DeliveryDto>

    fun getDeliveryById(id: String, requireDto: Boolean = false): EntityWithDto<Delivery, DeliveryDto>

    fun getAllDeliveries(requireDto: Boolean = false): EntityWithDtoList<Delivery, DeliveryDto>

    fun deleteDelivery(id: String)
}