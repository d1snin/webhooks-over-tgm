package dev.d1s.wot.client.util

import dev.d1s.wot.client.builder.regular
import dev.d1s.wot.client.builder.textSources

internal fun String.toTextSourceList() = textSources {
    regular(this@toTextSourceList)
}