package dev.d1s.wot.client.factory

import dev.d1s.teabag.dto.DtoConverter
import dev.d1s.wot.client.api.WotClient
import dev.d1s.wot.client.converter.impl.DeliveryDtoConverter
import dev.d1s.wot.client.converter.impl.TargetDtoConverter
import dev.d1s.wot.client.converter.impl.WebhookDtoConverter
import dev.d1s.wot.client.entity.delivery.Delivery
import dev.d1s.wot.client.entity.target.Target
import dev.d1s.wot.client.entity.webhook.Webhook
import dev.d1s.wot.commons.dto.delivery.DeliveryDto
import dev.d1s.wot.commons.dto.target.TargetDto
import dev.d1s.wot.commons.dto.webhook.WebhookDto

internal fun deliveryDtoConverter(wotClient: WotClient): DtoConverter<DeliveryDto, Delivery> =
    DeliveryDtoConverter(wotClient)

internal fun targetDtoConverter(wotClient: WotClient): DtoConverter<TargetDto, Target> =
    TargetDtoConverter(wotClient)

internal fun webhookDtoConverter(wotClient: WotClient): DtoConverter<WebhookDto, Webhook> =
    WebhookDtoConverter(wotClient)