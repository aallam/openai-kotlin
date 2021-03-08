package com.aallam.openai.client.internal

import com.aallam.openai.api.completion.CompletionRequest
import com.aallam.openai.api.completion.TextCompletion
import com.aallam.openai.api.engine.Engine
import com.aallam.openai.api.engine.EngineId
import com.aallam.openai.api.engine.EnginesResponse
import com.aallam.openai.api.search.SearchRequest
import com.aallam.openai.api.search.SearchResponse
import com.aallam.openai.api.search.SearchResult
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIConfig
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.utils.*

internal class OpenAIApi(config: OpenAIConfig) : OpenAI {

    private val httpClient: HttpClient = createHttpClient(config)

    override suspend fun search(
        engineId: EngineId,
        request: SearchRequest
    ): List<SearchResult> {
        return httpClient.post<SearchResponse>(
            path = "/v1/engines/$engineId/search",
            body = request
        ).data
    }

    override suspend fun engines(): List<Engine> {
        return httpClient.get<EnginesResponse>(path = "/v1/engines").data
    }

    override suspend fun engine(engineId: EngineId): Engine {
        return httpClient.get(path = "/v1/engines/$engineId")
    }

    override suspend fun createCompletion(engineId: EngineId, request: CompletionRequest?): TextCompletion {
        return httpClient.post(path = "/v1/engines/$engineId/completions", body = request ?: EmptyContent)
    }
}
