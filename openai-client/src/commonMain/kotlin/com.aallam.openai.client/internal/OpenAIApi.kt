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
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.utils.*
import io.ktor.utils.io.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

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

    override suspend fun completion(engineId: EngineId, request: CompletionRequest?): TextCompletion {
        return httpClient.post(path = "/v1/engines/$engineId/completions", body = request ?: EmptyContent)
    }

    override fun completions(engineId: EngineId, request: CompletionRequest?): Flow<TextCompletion> {
        return flow {
            httpClient.post<HttpStatement>(
                path = "/v1/engines/$engineId/completions",
                body = request ?: EmptyContent
            ).execute {
                val channel = it.receive<ByteReadChannel>()
                while (!channel.isClosedForRead) {
                    val line = channel.readUTF8Line() ?: continue
                    if (!line.startsWith(STREAM_PREFIX)) continue
                    if (line.startsWith(STREAM_END_TOKEN)) break
                    val raw = line.removePrefix(STREAM_PREFIX)
                    val value = JsonLenient.decodeFromString(TextCompletion.serializer(), raw)
                    emit(value)
                }
            }
        }
    }

    companion object {
        private const val STREAM_PREFIX = "data:"
        private const val STREAM_END_TOKEN = "$STREAM_PREFIX [DONE]"
    }
}
