[![](https://jitpack.io/v/dev.d1s/webhooks-over-tgm.svg)](https://jitpack.io/#dev.d1s/webhooks-over-tgm)

### Spring Boot Starter for the WoT Kotlin library

This starter autoconfigures `WotClient` and `WotWebhookClientCluster`.

### Installation

The binaries are server over Jitpack.

```kotlin
repositories {
    maven(url = "https://jitpack.io")
}

val wotClientStarterVersion: String by project

dependencies {
    implementation("dev.d1s.webhooks-over-tgm:wot-client-starter:$wotClientStarterVersion")
}
```

### Enabling `WotClient` autoconfiguration

```kotlin
@Configuration
@EnableWotClient
class WotClientConfiguration
```

The following properties will be observed:

- `wot.client.base-url` ***(required)***
- `wot.client.authorization`
- `wot.client.long-polling-recipient`

### Enabling `WotWebhookClientCluster` autoconfiguration

```kotlin
@Configuration
@EnableWotWebhookClientCluster
class WotWebhookClientClusterConfiguration
```

The following property will be observed: `wot.client.webhook-cluster.webhooks`. Note that you must provide a
comma-separated list of webhook URLs as the property value (e.g.: `url1,url2,url3`).