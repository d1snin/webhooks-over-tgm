package dev.d1s.wot.client.factory

import dev.d1s.wot.client.api.WotClient
import dev.d1s.wot.client.api.WotClientImpl

public fun wotClient(baseUrl: String, authorization: String? = null, longPollingRecipient: String? = null): WotClient =
    WotClientImpl(baseUrl, authorization, longPollingRecipient)