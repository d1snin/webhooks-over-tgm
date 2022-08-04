package dev.d1s.wot.client.api

import dev.d1s.wot.client.entity.content.TextSourceList

internal class WotWebhookClientClusterImpl(

    override val clients: List<WotWebhookClient>

) : WotWebhookClientCluster {

    override suspend fun publish(textSource: TextSourceList) = clients.map {
        it.publish(textSource)
    }
}