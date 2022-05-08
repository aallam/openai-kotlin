package com.aallam.openai.client.internal.api

import com.aallam.openai.api.completion.CompletionRequest
import com.aallam.openai.api.completion.TextCompletion
import com.aallam.openai.api.engine.EngineId
import com.aallam.openai.client.Completions
import com.aallam.openai.client.internal.JsonLenient
import com.aallam.openai.client.internal.api.EnginesApi.Companion.EnginesPath
import com.aallam.openai.client.internal.extension.toStreamRequest
import com.aallam.openai.client.internal.http.HttpTransport
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.client.utils.EmptyContent
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.readUTF8Line
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.decodeFromString

/**
 * Implementation of [Completions].
 */
internal class CompletionsApi(private val httpRequester: HttpTransport) : Completions {

    override suspend fun completion(engineId: EngineId, request: CompletionRequest?): TextCompletion {
        return httpRequester.perform {
            it.post {
                url(path = "$EnginesPath/$engineId/completions")
                setBody(request ?: EmptyContent)
                contentType(ContentType.Application.Json)
            }.body()
        }
    }

    override fun completions(engineId: EngineId, request: CompletionRequest?): Flow<TextCompletion> {
        return flow {
            val response = httpRequester.perform<HttpResponse> {
                it.post {
                    url(path = "$EnginesPath/$engineId/completions")
                    setBody(request.toStreamRequest())
                    contentType(ContentType.Application.Json)
                }
            }
            val readChannel = response.body<ByteReadChannel>()
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

    companion object {
        private const val StreamPrefix = "data:"
        private const val StreamEndToken = "$StreamPrefix [DONE]"
    }
}
