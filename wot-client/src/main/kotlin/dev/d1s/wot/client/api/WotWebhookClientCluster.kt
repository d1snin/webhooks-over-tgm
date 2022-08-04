package dev.d1s.wot.client.api

import dev.d1s.wot.client.entity.content.TextSourceList
import dev.d1s.wot.commons.dto.delivery.DeliveryDto

public sealed interface WotWebhookClientCluster : ContentPublisher<List<Result<DeliveryDto>>> {

    public val clients: List<WotWebhookClient>

    override suspend fun publish(textSource: TextSourceList): List<Result<DeliveryDto>>
}