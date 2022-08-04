package dev.d1s.wot.client.util

import dev.d1s.wot.client.exception.WotException

internal inline fun <R> runHandling(block: () -> R): Result<R> =
    runCatching {
        try {
            block()
        } catch (e: Exception) {
            throw WotException(e.message, e.cause)
        }
    }
