package dev.d1s.wot.client.factory

import dev.d1s.wot.client.api.WotWebhookClient
import dev.d1s.wot.client.api.WotWebhookClientImpl
import io.ktor.http.*

public fun wotWebhookClient(baseUrl: String, nonce: String): WotWebhookClient = WotWebhookClientImpl(baseUrl, nonce)

public fun wotWebhookClient(url: String): WotWebhookClient {
    val parsedUrl = Url(url)

    return wotWebhookClient(parsedUrl.protocolWithAuthority, parsedUrl.encodedPath.drop(1))
}