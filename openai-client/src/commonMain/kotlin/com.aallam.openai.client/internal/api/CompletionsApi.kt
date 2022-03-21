package com.aallam.openai.client.internal.api

import com.aallam.openai.api.completion.CompletionRequest
import com.aallam.openai.api.completion.TextCompletion
import com.aallam.openai.api.engine.EngineId
import com.aallam.openai.client.Completions
import com.aallam.openai.client.internal.api.EnginesApi.Companion.EnginesPath
import com.aallam.openai.client.internal.extension.toStreamRequest
import com.aallam.openai.client.internal.http.HttpTransport
import com.aallam.openai.client.internal.http.streamSSE
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.utils.EmptyContent
import kotlinx.coroutines.flow.Flow

/**
 * Implementation of [Completions].
 */
internal class CompletionsApi(private val httpRequester: HttpTransport) : Completions {

    override suspend fun completion(engineId: EngineId, request: CompletionRequest?): TextCompletion {
        return httpRequester.perform {
            it.post {
                url(path = "$EnginesPath/$engineId/completions")
                setBody(request ?: EmptyContent)
            }.body()
        }
    }

    override fun completions(engineId: EngineId, request: CompletionRequest?): Flow<TextCompletion> {
        return streamSSE {
            httpRequester.perform {
                it.post {
                    url(path = "$EnginesPath/$engineId/completions")
                    setBody(request.toStreamRequest())
                }
            }
        }
    }
}
