package com.aallam.openai.client.internal

import com.aallam.openai.client.OpenAIConfig
import com.aallam.openai.client.internal.extension.toKtorLogLevel
import com.aallam.openai.client.internal.extension.toKtorLogger
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.*
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
                accept(ContentType.Application.Json)
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
