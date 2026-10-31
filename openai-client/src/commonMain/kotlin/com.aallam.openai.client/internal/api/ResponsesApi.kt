package com.aallam.openai.client.internal.api

import com.aallam.openai.api.exception.OpenAIAPIException
import com.aallam.openai.api.core.DeleteResponse
import com.aallam.openai.api.core.PaginatedList
import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.core.SortOrder
import com.aallam.openai.api.response.Response
import com.aallam.openai.api.response.ResponseId
import com.aallam.openai.api.response.ResponseInputItem
import com.aallam.openai.api.response.*
import com.aallam.openai.client.internal.extension.streamRequestOf
import com.aallam.openai.client.internal.JsonLenient
import io.ktor.utils.io.*
import kotlinx.serialization.json.*
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.isActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import com.aallam.openai.client.Responses
import com.aallam.openai.client.ResponseConnection
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import com.aallam.openai.client.internal.extension.requestOptions
import com.aallam.openai.client.internal.http.HttpRequester
import com.aallam.openai.client.internal.http.perform
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

internal class ResponsesApi(private val requester: HttpRequester) : Responses {
    override suspend fun response(
        request: ResponseRequest,
        requestOptions: RequestOptions?
    ): Response {
        return requester.perform {
            it.post {
                url(path = ApiPath.Responses)
                setBody(request)
                contentType(ContentType.Application.Json)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun response(id: ResponseId, requestOptions: RequestOptions?): Response? = onNotFound(null) {
        requester.perform<Response> {
            it.get {
                url(path = "${ApiPath.Responses}/${id.id}")
                requestOptions(requestOptions)
            }
        }
    }

    override suspend fun delete(id: ResponseId, requestOptions: RequestOptions?): Boolean = onNotFound(false) {
        requester.perform<DeleteResponse> {
            it.delete {
                url(path = "${ApiPath.Responses}/${id.id}")
                requestOptions(requestOptions)
            }
        }.deleted
    }

    override suspend fun cancel(id: ResponseId, requestOptions: RequestOptions?): Response? = onNotFound(null) {
        requester.perform<Response> {
            it.post {
                url(path = "${ApiPath.Responses}/${id.id}/cancel")
                requestOptions(requestOptions)
            }
        }
    }

    override suspend fun responseInputItems(
        id: ResponseId,
        limit: Int?,
        order: SortOrder?,
        after: String?,
        before: String?,
        requestOptions: RequestOptions?
    ): PaginatedList<ResponseInputItem> {
        return requester.perform {
            it.get {
                url(path = "${ApiPath.Responses}/${id.id}/input_items") {
                    limit?.let { value -> parameter("limit", value) }
                    order?.let { value -> parameter("order", value.order) }
                    after?.let { value -> parameter("after", value) }
                    before?.let { value -> parameter("before", value) }
                }
                requestOptions(requestOptions)
            }.body()
        }
    }
    override fun streamResponses(request: ResponseCreateRequest, requestOptions: RequestOptions?): Flow<ResponseStreamEvent> =
        responseEvents(HttpRequestBuilder().apply {
            method = HttpMethod.Post
            url(path = ApiPath.Responses)
            setBody(streamRequestOf(request))
            contentType(ContentType.Application.Json)
            accept(ContentType.Text.EventStream)
            requestOptions(requestOptions)
        })

    override fun resumeResponse(
        id: ResponseId,
        startingAfter: Int?,
        include: List<String>?,
        includeObfuscation: Boolean?,
        requestOptions: RequestOptions?,
    ): Flow<ResponseStreamEvent> = responseEvents(HttpRequestBuilder().apply {
        method = HttpMethod.Get
        url(path = "${ApiPath.Responses}/${id.id}")
        parameter("stream", true)
        startingAfter?.let { parameter("starting_after", it) }
        include?.forEach { parameter("include[]", it) }
        includeObfuscation?.let { parameter("include_obfuscation", it) }
        accept(ContentType.Text.EventStream)
        requestOptions(requestOptions)
    })

    override suspend fun createResponse(request: ResponseCreateRequest, requestOptions: RequestOptions?): ResponseResult = requester.perform {
        it.post {
            url(path = ApiPath.Responses)
            setBody(request)
            contentType(ContentType.Application.Json)
            requestOptions(requestOptions)
        }.body()
    }

    override suspend fun cancelResponse(id: ResponseId, requestOptions: RequestOptions?): ResponseResult? = onNotFound(null) {
        requester.perform<ResponseResult> {
            it.post {
                url(path = "${ApiPath.Responses}/${id.id}/cancel")
                requestOptions(requestOptions)
            }
        }
    }

    override suspend fun retrieveResponse(id: ResponseId, include: List<String>, requestOptions: RequestOptions?): ResponseResult? = onNotFound(null) {
        requester.perform<ResponseResult> {
            it.get {
                url(path = "${ApiPath.Responses}/${id.id}")
                include.forEach { parameter("include[]", it) }
                requestOptions(requestOptions)
            }
        }
    }

    override suspend fun responseInputItemsPage(
        id: ResponseId,
        include: List<String>,
        limit: Int?,
        order: SortOrder?,
        after: String?,
        before: String?,
        requestOptions: RequestOptions?,
    ): PaginatedList<ResponseInputEntry> = requester.perform {
        it.get {
            url(path = "${ApiPath.Responses}/${id.id}/input_items")
            include.forEach { parameter("include[]", it) }
            limit?.let { parameter("limit", it) }
            order?.let { parameter("order", it.order) }
            after?.let { parameter("after", it) }
            before?.let { parameter("before", it) }
            requestOptions(requestOptions)
        }.body()
    }

    private suspend fun <T> onNotFound(default: T, block: suspend () -> T): T = try {
        block()
    } catch (exception: OpenAIAPIException) {
        if (exception.statusCode == HttpStatusCode.NotFound.value) default else throw exception
    }

    override suspend fun compactResponse(request: ResponseCompactRequest, requestOptions: RequestOptions?): ResponseCompaction =
        requester.perform {
            it.post {
                url(path = "${ApiPath.Responses}/compact")
                setBody(request)
                contentType(ContentType.Application.Json)
                requestOptions(requestOptions)
            }.body()
        }

    override suspend fun responseInputTokens(request: ResponseTokenCountRequest, requestOptions: RequestOptions?): ResponseTokenCount =
        requester.perform {
            it.post {
                url(path = "${ApiPath.Responses}/input_tokens")
                setBody(request)
                contentType(ContentType.Application.Json)
                requestOptions(requestOptions)
            }.body()
        }

    override suspend fun <T> responseSession(
        secure: Boolean,
        requestOptions: RequestOptions?,
        block: suspend ResponseConnection.() -> T,
    ): T {
        val socket = requester.realtimeSocket {
            url(path = ApiPath.Responses) { protocol = if (secure) URLProtocol.WSS else URLProtocol.WS }
            requestOptions(requestOptions)
        }
        val connection = ResponseSocketConnection(socket)
        try {
            return connection.block()
        } finally {
            withContext(NonCancellable) { connection.close() }
        }
    }

    private fun responseEvents(builder: HttpRequestBuilder): Flow<ResponseStreamEvent> = channelFlow {
        requester.perform(builder) { response ->
            val channel: ByteReadChannel = response.body()
            val data = mutableListOf<String>()
            suspend fun dispatch() {
                if (data.isNotEmpty()) {
                    val payload = data.joinToString("\n")
                    data.clear()
                    if (payload != "[DONE]") send(JsonLenient.decodeFromString(ResponseStreamEvent.serializer(), payload))
                }
            }
            try {
                while (currentCoroutineContext().isActive) {
                    val line = channel.readUTF8Line() ?: break
                    when {
                        line.isEmpty() -> dispatch()
                        line.startsWith("data:") -> data += line.removePrefix("data:").removePrefix(" ")
                    }
                }
                dispatch()
            } finally {
                channel.cancel()
            }
        }
    }
}
