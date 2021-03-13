package com.aallam.openai.client.internal

import com.aallam.openai.client.OpenAIConfig
import com.aallam.openai.client.internal.extension.toKLogLevel
import com.aallam.openai.client.internal.extension.toKLogger
import io.ktor.client.*
import io.ktor.client.features.*
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
            logger = config.logger.toKLogger()
            level = config.logLevel.toKLogLevel()
        }
        defaultRequest {
            url {
                protocol = URLProtocol.HTTPS
                host = "api.openai.com"
                header(HttpHeaders.Authorization, "Bearer ${config.token}")
                contentType(ContentType.Application.Json)
            }
        }
    }
}

/**
 * Internal Json Serializer.
 */
internal val JsonLenient = Json {
    ignoreUnknownKeys = true
}
