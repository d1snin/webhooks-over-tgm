package dev.d1s.wot.client.api

import dev.d1s.wot.client.entity.content.TextSourceList
import dev.d1s.wot.commons.dto.delivery.DeliveryDto

public sealed interface WotWebhookClient : BaseUrlAware, ContentPublisher<Result<DeliveryDto>> {

    public val nonce: String

    override suspend fun publish(textSource: TextSourceList): Result<DeliveryDto>
}