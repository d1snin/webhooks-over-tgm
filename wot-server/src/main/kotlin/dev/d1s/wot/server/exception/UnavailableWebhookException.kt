package dev.d1s.wot.server.exception

import dev.d1s.advice.entity.ErrorResponseData
import dev.d1s.advice.exception.HttpStatusException
import org.springframework.http.HttpStatus

private const val UNAVAILABLE_WEBHOOK_MESSAGE = "Webhook %s is not currently available."

class UnavailableWebhookException(webhook: String) : HttpStatusException(
    ErrorResponseData(
        HttpStatus.FORBIDDEN,
        UNAVAILABLE_WEBHOOK_MESSAGE.format(webhook)
    )
)