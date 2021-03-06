package com.aallam.openai.client.internal

import com.aallam.openai.client.OpenAIConfig
import io.ktor.client.HttpClient
import io.ktor.client.features.defaultRequest
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.features.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.URLProtocol
import io.ktor.http.contentType
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
      logger = config.logger
      level = config.logLevel
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
