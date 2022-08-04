package dev.d1s.wot.client.factory

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import dev.d1s.wot.client.api.BaseUrlAware
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*

internal fun httpClient(
    baseUrlAware: BaseUrlAware,
    authorization: String? = null
) = HttpClient(CIO) {
    expectSuccess = true

    install(ContentNegotiation) {
        jackson {
            registerModule(
                JavaTimeModule()
            )
        }
    }

    defaultRequest {
        url(baseUrlAware.baseUrl)

        authorization?.let {
            header(HttpHeaders.Authorization, it)
        }
    }
}