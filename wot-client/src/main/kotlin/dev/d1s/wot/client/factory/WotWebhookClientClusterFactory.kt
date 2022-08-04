package dev.d1s.wot.client.factory

import dev.d1s.wot.client.api.WotWebhookClient
import dev.d1s.wot.client.api.WotWebhookClientCluster
import dev.d1s.wot.client.api.WotWebhookClientClusterImpl
import dev.d1s.wot.client.marker.WotClientDsl

@WotClientDsl
public class WotWebhookLocationsBuilder {

    internal val locations = mutableMapOf<String, String>()

    public infix fun String.withNonce(nonce: String) {
        locations += this to nonce
    }
}

public fun wotWebhookClientCluster(clients: List<WotWebhookClient>): WotWebhookClientCluster =
    WotWebhookClientClusterImpl(clients)

public fun wotWebhookClientClusterByUrls(urls: List<String>): WotWebhookClientCluster =
    WotWebhookClientClusterImpl(urls.map {
        wotWebhookClient(it)
    })

public fun wotWebhookClientClusterByUrls(firstUrl: String, vararg others: String): WotWebhookClientCluster =
    wotWebhookClientClusterByUrls(others.asList() + firstUrl)

public fun wotWebhookClientCluster(builder: WotWebhookLocationsBuilder.() -> Unit): WotWebhookClientCluster =
    WotWebhookClientClusterImpl(WotWebhookLocationsBuilder().apply(builder).locations.map {
        wotWebhookClient(it.key, it.value)
    })