package com.aallam.openai.client.internal

import com.aallam.openai.client.OpenAIConfig
import com.aallam.openai.client.internal.extension.toKtorLogLevel
import com.aallam.openai.client.internal.extension.toKtorLogger
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.features.auth.*
import io.ktor.client.features.auth.providers.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.json.Json

/**
 * Default Http Client.
 */
internal fun createHttpClient(config: OpenAIConfig): HttpClient {
    return HttpClient {
        install(JsonFeature) {
            serializer = KotlinxSerializer(JsonLenient)
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
    useAlternativeNames = false // TODO: remove after https://github.com/Kotlin/kotlinx.serialization/issues/1450
}
