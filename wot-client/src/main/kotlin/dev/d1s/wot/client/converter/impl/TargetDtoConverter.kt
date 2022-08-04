package dev.d1s.wot.client.converter.impl

import dev.d1s.wot.client.api.WotClient
import dev.d1s.wot.client.converter.AbstractDtoConverter
import dev.d1s.wot.client.entity.target.Target
import dev.d1s.wot.commons.dto.target.TargetDto

internal class TargetDtoConverter(private val wotClient: WotClient) : AbstractDtoConverter<TargetDto, Target>() {

    override fun convertToEntity(dto: TargetDto) = dto.run {
        Target(id, chatId, available, deferred {
            webhooks.map {
                wotClient.getWebhook(it)
            }
        })
    }
}