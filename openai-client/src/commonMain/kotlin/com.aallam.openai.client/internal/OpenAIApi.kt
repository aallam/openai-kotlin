package com.aallam.openai.client.internal

import com.aallam.openai.api.engine.Engine
import com.aallam.openai.api.engine.EngineId
import com.aallam.openai.api.engine.EnginesResponse
import com.aallam.openai.api.search.SearchRequest
import com.aallam.openai.api.search.SearchResponse
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIConfig
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.post

internal class OpenAIApi(config: OpenAIConfig) : OpenAI {

  private val httpClient: HttpClient = createHttpClient(config)

  override suspend fun search(
    engineId: EngineId,
    request: SearchRequest
  ): SearchResponse {
    return httpClient.post(
      path = "/v1/engines/$engineId/search",
      body = request
    )
  }

  override suspend fun engines(): EnginesResponse {
    return httpClient.get(path = "/v1/engines")
  }

  override suspend fun engine(engineId: EngineId): Engine {
    return httpClient.get(path = "/v1/engines/$engineId")
  }
}
