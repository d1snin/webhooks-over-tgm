package dev.d1s.wot.client.converter.impl

import dev.d1s.wot.client.api.WotClient
import dev.d1s.wot.client.converter.AbstractDtoConverter
import dev.d1s.wot.client.entity.delivery.Delivery
import dev.d1s.wot.commons.dto.delivery.DeliveryDto

internal class DeliveryDtoConverter(private val wotClient: WotClient) : AbstractDtoConverter<DeliveryDto, Delivery>() {

    override fun convertToEntity(dto: DeliveryDto) = dto.run {
        Delivery(id, content, successful, time, deferred {
            wotClient.getWebhook(webhook)
        })
    }
}