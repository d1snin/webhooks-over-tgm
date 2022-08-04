package dev.d1s.wot.client.exception

public class WotException(
    message: String?,
    cause: Throwable?
) : RuntimeException(message, cause)