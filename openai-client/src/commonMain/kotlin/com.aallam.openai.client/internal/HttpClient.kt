package com.aallam.openai.client.internal

import com.aallam.openai.client.OpenAIConfig
import com.aallam.openai.client.internal.extension.toKtorLogLevel
import com.aallam.openai.client.internal.extension.toKtorLogger
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.URLProtocol
import io.ktor.serialization.kotlinx.KotlinxSerializationConverter
import kotlinx.serialization.json.Json
import kotlin.time.DurationUnit

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

        install(HttpTimeout) {
            config.timeout.socket?.let { socketTimeout ->
                socketTimeoutMillis = socketTimeout.toLong(DurationUnit.MILLISECONDS)
            }
            config.timeout.connect?.let { connectTimeout ->
                connectTimeoutMillis = connectTimeout.toLong(DurationUnit.MILLISECONDS)
            }
            config.timeout.request?.let { requestTimeout ->
                requestTimeoutMillis = requestTimeout.toLong(DurationUnit.MILLISECONDS)
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
