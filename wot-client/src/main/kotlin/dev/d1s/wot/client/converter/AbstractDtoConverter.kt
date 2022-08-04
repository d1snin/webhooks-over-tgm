package dev.d1s.wot.client.converter

import dev.d1s.teabag.dto.DtoConverter
import kotlinx.coroutines.*

internal abstract class AbstractDtoConverter<D : Any, E : Any> : DtoConverter<D, E> {

    protected val deferredPropertyFetchingScope = CoroutineScope(Dispatchers.IO)

    protected inline fun <R> deferred(crossinline block: suspend () -> R): Deferred<R> =
        deferredPropertyFetchingScope.async(start = CoroutineStart.LAZY) {
            block()
        }
}