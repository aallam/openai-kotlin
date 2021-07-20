package com.aallam.openai.client.internal.api

import com.aallam.openai.api.completion.CompletionRequest
import com.aallam.openai.api.completion.TextCompletion
import com.aallam.openai.api.engine.EngineId
import com.aallam.openai.client.Completions
import com.aallam.openai.client.internal.JsonLenient
import com.aallam.openai.client.internal.api.EnginesApi.Companion.EnginesPath
import com.aallam.openai.client.internal.extension.toStreamRequest
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.utils.*
import io.ktor.http.*
import io.ktor.utils.io.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.decodeFromString

/**
 * Implementation of [Completions].
 */
internal class CompletionsApi(private val httpClient: HttpClient) : Completions {

    override suspend fun completion(engineId: EngineId, request: CompletionRequest?): TextCompletion {
        return httpClient.post(path = "$EnginesPath/$engineId/completions", body = request ?: EmptyContent) {
            contentType(ContentType.Application.Json)
        }
    }

    override fun completions(engineId: EngineId, request: CompletionRequest?): Flow<TextCompletion> {
        return flow {
            httpClient.post<HttpStatement>(
                path = "$EnginesPath/$engineId/completions",
                body = request.toStreamRequest()
            ) {
                contentType(ContentType.Application.Json)
            }.execute { response ->
                val readChannel = response.receive<ByteReadChannel>()
                while (!readChannel.isClosedForRead) {
                    val line = readChannel.readUTF8Line() ?: ""
                    val value: TextCompletion = when {
                        line.startsWith(StreamEndToken) -> break
                        line.startsWith(StreamPrefix) -> JsonLenient.decodeFromString(line.removePrefix(StreamPrefix))
                        else -> continue
                    }
                    emit(value)
                }
            }
        }
    }

    companion object {
        private const val StreamPrefix = "data:"
        private const val StreamEndToken = "$StreamPrefix [DONE]"
    }
}