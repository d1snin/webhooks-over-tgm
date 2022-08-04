/*
 * Copyright 2022 Webhooks over Telegram project contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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