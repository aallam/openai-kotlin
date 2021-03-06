package com.aallam.openai.client.internal

import com.aallam.openai.api.Engine
import com.aallam.openai.api.OpenAIResponse
import com.aallam.openai.api.search.SearchRequest
import com.aallam.openai.api.search.SearchResponse
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIConfig
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

internal class OpenAIApi(config: OpenAIConfig) : OpenAI {

  private val httpClient: HttpClient = createHttpClient(config)

  override suspend fun search(
    engine: Engine,
    request: SearchRequest
  ): OpenAIResponse<SearchResponse> {
    return httpClient.post(
      path = "/v1/engines/$engine/search",
      body = request
    )
  }
}
