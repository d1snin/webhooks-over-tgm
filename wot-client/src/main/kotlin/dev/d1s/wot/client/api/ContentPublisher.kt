package dev.d1s.wot.client.api

import dev.d1s.wot.client.builder.regular
import dev.d1s.wot.client.builder.textSources
import dev.d1s.wot.client.entity.content.MutableTextSourceList
import dev.d1s.wot.client.entity.content.TextSourceList

public sealed interface ContentPublisher<C> {

    public suspend fun publish(textSource: TextSourceList): C

    public suspend fun publish(builder: MutableTextSourceList.() -> Unit): C = this.publish(
        textSources(builder)
    )

    public suspend fun publish(regularText: String): C = this.publish(textSources {
        regular(regularText)
    })
}