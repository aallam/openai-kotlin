package com.aallam.openai.client.internal.api

import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionChunk
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatCompletionOptions
import com.aallam.openai.api.chat.ChatCompletionResult
import com.aallam.openai.api.chat.ChatResultMessage
import com.aallam.openai.client.internal.JsonLenient
import kotlinx.serialization.json.*
import com.aallam.openai.api.chat.ChatCompletionListQuery
import com.aallam.openai.api.chat.ChatCompletionUpdate
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.core.*
import com.aallam.openai.api.exception.OpenAIAPIException
import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.client.Chat
import com.aallam.openai.client.internal.extension.requestOptions
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
    override suspend fun chatCompletion(
        request: ChatCompletionRequest,
        requestOptions: RequestOptions?
    ): ChatCompletion {
        return requester.perform {
            it.post {
                url(path = ApiPath.ChatCompletions)
                setBody(request)
                contentType(ContentType.Application.Json)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override fun chatCompletions(
        request: ChatCompletionRequest,
        requestOptions: RequestOptions?
    ): Flow<ChatCompletionChunk> {
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
            requestOptions(requestOptions)
        }
        return flow {
            requester.perform(builder) { response -> streamEventsFrom(response) }
        }
    }

    override suspend fun chatCompletion(id: String, requestOptions: RequestOptions?): ChatCompletionResult? =
        try {
            requester.perform {
                it.get {
                    url(path = "${ApiPath.ChatCompletions}/$id")
                    requestOptions(requestOptions)
                }.body()
            }
        } catch (e: OpenAIAPIException) {
            if (e.statusCode == HttpStatusCode.NotFound.value) null else throw e
        }

    override suspend fun chatCompletions(
        query: ChatCompletionListQuery,
        requestOptions: RequestOptions?
    ): PaginatedList<ChatCompletionResult> =
        requester.perform {
            it.get {
                url(path = ApiPath.ChatCompletions) {
                    query.after?.let { parameter("after", it) }
                    query.limit?.let { parameter("limit", it) }
                    query.model?.let { parameter("model", it.id) }
                    query.order?.let { parameter("order", it.order) }
                    query.metadata?.forEach { (key, value) -> parameter("metadata[$key]", value) }
                }
                requestOptions(requestOptions)
            }.body()
        }

    override suspend fun updateChatCompletion(
        id: String,
        request: ChatCompletionUpdate,
        requestOptions: RequestOptions?
    ): ChatCompletionResult =
        requester.perform {
            it.post {
                url(path = "${ApiPath.ChatCompletions}/$id")
                contentType(ContentType.Application.Json)
                setBody(request)
                requestOptions(requestOptions)
            }.body()
        }

    override suspend fun deleteChatCompletion(id: String, requestOptions: RequestOptions?): Boolean =
        try {
            requester.perform<DeleteResponse> {
                it.delete {
                    url(path = "${ApiPath.ChatCompletions}/$id")
                    requestOptions(requestOptions)
                }
            }.deleted
        } catch (e: OpenAIAPIException) {
            if (e.statusCode == HttpStatusCode.NotFound.value) false else throw e
        }

    override suspend fun chatCompletionMessages(
        id: String,
        after: String?,
        limit: Int?,
        order: SortOrder?,
        requestOptions: RequestOptions?
    ): PaginatedList<ChatResultMessage> =
        requester.perform {
            it.get {
                url(path = "${ApiPath.ChatCompletions}/$id/messages") {
                    after?.let { parameter("after", it) }
                    limit?.let { parameter("limit", it) }
                    order?.let { parameter("order", it.order) }
                }
                requestOptions(requestOptions)
            }.body()
        }

    override suspend fun chatCompletion(request: ChatCompletionRequest, options: ChatCompletionOptions, requestOptions: RequestOptions?): ChatCompletionResult =
        requester.perform {
            it.post {
                url(path = ApiPath.ChatCompletions)
                contentType(ContentType.Application.Json)
                setBody(advancedBody(request, options))
                requestOptions(requestOptions)
            }.body()
        }

    override fun chatCompletions(request: ChatCompletionRequest, options: ChatCompletionOptions, requestOptions: RequestOptions?): Flow<ChatCompletionResult> {
        val builder = HttpRequestBuilder().apply {
            method = HttpMethod.Post
            url(path = ApiPath.ChatCompletions)
            contentType(ContentType.Application.Json)
            accept(ContentType.Text.EventStream)
            setBody(streamRequestOf(advancedBody(request, options)))
            requestOptions(requestOptions)
        }
        return flow { requester.perform(builder) { response -> streamEventsFrom(response) } }
    }

    private fun advancedBody(request: ChatCompletionRequest, options: ChatCompletionOptions): JsonObject =
        JsonObject(JsonLenient.encodeToJsonElement(request).jsonObject + JsonLenient.encodeToJsonElement(options).jsonObject)
}
