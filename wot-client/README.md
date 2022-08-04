[![](https://jitpack.io/v/dev.d1s/webhooks-over-tgm.svg)](https://jitpack.io/#dev.d1s/webhooks-over-tgm)

### Kotlin library for WoT

This is the Kotlin client for the WoT public and private APIs. With which you can send messages to webhooks, manage
entities and receive events.

### Installation

WoT library binaries are served over Jitpack.

```kotlin 
repositories {
    maven(url = "https://jitpack.io")
}

val wotClientVersion: String by project

dependencies {
    implementation("dev.d1s.webhooks-over-tgm:wot-client:$wotClientVersion")
}
```

### Usage

The library's API is pretty simple and is easy to use.

#### Using client to access the main API

```kotlin
suspend fun main() {
    val client = wotClient(
        baseUrl = "https://wot.example.com",
        authorization = "your_secret_key"
    )

    val webhook = client.postWebhook(
        name = "yet-another-webhook",
        botToken = "telegram_bot_token"
    ).getOrThrow()

    webhook.postDelivery(
        webhook,
        textSources {
            regular("Hello, World!")
        }
    )

    client.deleteWebhook(webhook)
}
```

#### Using webhook client to access the public API

```kotlin
suspend fun main() {
    val webhook = wotWebhookClient(
        baseUrl = "https://wot.example.com",
        nonce = "webhook_nonce"
    )

    webhook.publish {
        bold("Hello, World!")
    }
}
```

#### Using webhook client cluster to broadcast messages

```kotlin
suspend fun main() {
    val cluster = wotWebhookClientCluster {
        val baseUrl = "https://wot.example.com"

        baseUrl withNonce "first_nonce"
        baseUrl withnonce "second_nonce"
    }

    cluster.publish {
        bold("Hello, World!")
    }
}
```

#### Using long-polling API client

```kotlin
suspend fun main() {
    val client = wotClient(
        baseUrl = "https://wot.example.com",
        authorization = "your_secret_key"
    )

    val lp = client.longPollingClient

    lp.onWebhookCreated {
        val webhook = data

        println("A webhook with ID ${webhook.id} has been created at $timestamp.")
    }
}
```