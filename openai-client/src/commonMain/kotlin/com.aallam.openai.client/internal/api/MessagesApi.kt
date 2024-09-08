package com.aallam.openai.client.internal.api

import com.aallam.openai.api.core.PaginatedList
import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.core.SortOrder
import com.aallam.openai.api.message.Message
import com.aallam.openai.api.message.MessageId
import com.aallam.openai.api.message.MessageRequest
import com.aallam.openai.api.thread.ThreadId
import com.aallam.openai.client.Messages
import com.aallam.openai.client.internal.extension.beta
import com.aallam.openai.client.internal.extension.requestOptions
import com.aallam.openai.client.internal.http.HttpRequester
import com.aallam.openai.client.internal.http.perform
import com.aallam.openai.client.internal.http.performGetHeaders
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

internal class MessagesApi(val requester: HttpRequester) : Messages {
    override suspend fun message(
        threadId: ThreadId,
        request: MessageRequest,
        requestOptions: RequestOptions?
    ): Message {
        return requester.perform {
            it.post {
                url(path = "${ApiPath.Threads}/${threadId.id}/messages")
                setBody(request)
                contentType(ContentType.Application.Json)
                beta("assistants", 2)
                requestOptions(
                    requestOptions
                )
            }.body()
        }
    }

    override suspend fun message(threadId: ThreadId, messageId: MessageId, requestOptions: RequestOptions?): Message {
        return requester.perform {
            it.get {
                url(path = "${ApiPath.Threads}/${threadId.id}/messages/${messageId.id}")
                beta("assistants", 2)
                requestOptions(
                    requestOptions
                )
            }.body()
        }
    }

    override suspend fun message(
        threadId: ThreadId,
        messageId: MessageId,
        metadata: Map<String, String>?,
        requestOptions: RequestOptions?
    ): Message {
        return requester.perform {
            it.post {
                url(path = "${ApiPath.Threads}/${threadId.id}/messages/${messageId.id}")
                metadata?.let { meta ->
                    setBody(mapOf("metadata" to meta))
                    contentType(ContentType.Application.Json)
                }
                beta("assistants", 2)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun messages(
        threadId: ThreadId,
        limit: Int?,
        order: SortOrder?,
        after: MessageId?,
        before: MessageId?,
        requestOptions: RequestOptions?
    ): Pair<Headers, PaginatedList<Message>> {
        return requester.performGetHeaders{
            it.get {
                url(path = "${ApiPath.Threads}/${threadId.id}/messages") {
                    limit?.let { value -> parameter("limit", value) }
                    order?.let { value -> parameter("order", value.order) }
                    before?.let { value -> parameter("before", value.id) }
                    after?.let { value -> parameter("after", value.id) }
                }
                beta("assistants", 2)
                requestOptions(requestOptions)
            }.body()
        }
    }
}
