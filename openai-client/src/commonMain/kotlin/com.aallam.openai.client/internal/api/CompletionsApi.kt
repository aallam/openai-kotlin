package com.aallam.openai.client.internal.api

import com.aallam.openai.api.completion.CompletionRequest
import com.aallam.openai.api.completion.TextCompletion
import com.aallam.openai.client.Completions
import com.aallam.openai.client.internal.extension.streamEventsFrom
import com.aallam.openai.client.internal.extension.toStreamRequest
import com.aallam.openai.client.internal.http.HttpRequester
import com.aallam.openai.client.internal.http.perform
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.util.InternalAPI
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Implementation of [Completions].
 */
internal class CompletionsApi(private val requester: HttpRequester) : Completions {

    override suspend fun completion(request: CompletionRequest): TextCompletion {
        return requester.perform {
            it.post {
                url(path = CompletionsPathV1)
                setBody(request)
                contentType(ContentType.Application.Json)
            }.body()
        }
    }

    @OptIn(InternalAPI::class)
    override fun completions(request: CompletionRequest): Flow<TextCompletion> {
        val builder = HttpRequestBuilder().apply {
            method = HttpMethod.Post
            url(path = CompletionsPathV1)
            setBody(request.toStreamRequest())
            contentType(ContentType.Application.Json)
            accept(ContentType.Text.EventStream)
            headers {
                append(HttpHeaders.CacheControl, "no-cache")
                append(HttpHeaders.Connection, "keep-alive")
            }
        }
        return flow {
            requester.perform(builder) { response -> streamEventsFrom(response) }
        }
    }

    companion object {
        private const val CompletionsPathV1 = "v1/completions"
        private const val StreamPrefix = "data:"
        private const val StreamEndToken = "$StreamPrefix [DONE]"
    }
}
