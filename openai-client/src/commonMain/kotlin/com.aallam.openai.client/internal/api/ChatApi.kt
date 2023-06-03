package com.aallam.openai.client.internal.api

import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionChunk
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.client.Chat
import com.aallam.openai.client.internal.extension.streamEventsFrom
import com.aallam.openai.client.internal.extension.streamRequestOf
import com.aallam.openai.client.internal.http.HttpRequester
import com.aallam.openai.client.internal.http.perform
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal class ChatApi(private val requester: HttpRequester) : Chat {
    override suspend fun chatCompletion(request: ChatCompletionRequest): ChatCompletion {
        return requester.perform {
            it.post {
                url(path = ApiPath.ChatCompletions)
                setBody(request)
                contentType(ContentType.Application.Json)
            }.body()
        }
    }

    override fun chatCompletions(request: ChatCompletionRequest): Flow<ChatCompletionChunk> {
        val builder = HttpRequestBuilder().apply {
            method = HttpMethod.Post
            url(path = ApiPath.ChatCompletions)
            setBody(streamRequestOf(request))
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
}
