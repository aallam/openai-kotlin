package com.aallam.openai.client.internal

import com.aallam.openai.client.OpenAIConfig
import com.aallam.openai.client.internal.extension.toKtorLogLevel
import com.aallam.openai.client.internal.extension.toKtorLogger
import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.accept
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.URLProtocol
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.KotlinxSerializationConverter
import kotlinx.serialization.json.Json

/**
 * Default Http Client.
 */
internal fun createHttpClient(config: OpenAIConfig): HttpClient {
    return HttpClient {
        install(ContentNegotiation) {
            register(ContentType.Application.Json, KotlinxSerializationConverter(JsonLenient))
        }
        install(Logging) {
            logger = config.logger.toKtorLogger()
            level = config.logLevel.toKtorLogLevel()
        }
        install(Auth) {
            bearer {
                loadTokens {
                    BearerTokens(accessToken = config.token, refreshToken = "")
                }
            }
        }
        defaultRequest {
            url {
                protocol = URLProtocol.HTTPS
                host = "api.openai.com"
            }
        }
    }
}

/**
 * Internal Json Serializer.
 */
internal val JsonLenient = Json {
    isLenient = true
    ignoreUnknownKeys = true
}
