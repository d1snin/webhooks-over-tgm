package dev.d1s.wot.client.converter.impl

import dev.d1s.wot.client.api.WotClient
import dev.d1s.wot.client.converter.AbstractDtoConverter
import dev.d1s.wot.client.entity.webhook.Webhook
import dev.d1s.wot.commons.dto.webhook.WebhookDto

internal class WebhookDtoConverter(private val wotClient: WotClient) : AbstractDtoConverter<WebhookDto, Webhook>() {

    override fun convertToEntity(dto: WebhookDto) = dto.run {
        Webhook(id, name, botToken, private, deferred {
            targets.map {
                wotClient.getTarget(it)
            }
        })
    }
}