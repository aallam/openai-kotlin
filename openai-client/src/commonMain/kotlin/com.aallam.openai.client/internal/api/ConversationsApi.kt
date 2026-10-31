package com.aallam.openai.client.internal.api

import com.aallam.openai.api.conversation.*
import com.aallam.openai.api.core.DeleteResponse
import com.aallam.openai.api.core.PaginatedList
import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.core.SortOrder
import com.aallam.openai.api.exception.OpenAIAPIException
import com.aallam.openai.client.Conversations
import com.aallam.openai.client.internal.extension.requestOptions
import com.aallam.openai.client.internal.http.HttpRequester
import com.aallam.openai.client.internal.http.perform
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.json.*

internal class ConversationsApi(private val requester: HttpRequester) : Conversations {
    override suspend fun conversation(request: ConversationRequest, requestOptions: RequestOptions?): Conversation =
        requester.perform {
            it.post {
                url(path = ApiPath.Conversations)
                contentType(ContentType.Application.Json)
                setBody(request)
                requestOptions(requestOptions)
            }.body()
        }

    override suspend fun conversation(id: ConversationId, requestOptions: RequestOptions?): Conversation? =
        getOrNull("${ApiPath.Conversations}/${id.id}", null, requestOptions)

    override suspend fun updateConversation(
        id: ConversationId,
        request: ConversationUpdateRequest,
        requestOptions: RequestOptions?
    ): Conversation = requester.perform {
        it.post {
            url(path = "${ApiPath.Conversations}/${id.id}")
            contentType(ContentType.Application.Json)
            // Preserve explicit null rather than omitting metadata under explicitNulls=false.
            setBody(buildJsonObject {
                put("metadata", request.metadata?.let { metadata ->
                    buildJsonObject { metadata.forEach { (key, value) -> put(key, value) } }
                } ?: JsonNull)
            })
            requestOptions(requestOptions)
        }.body()
    }

    override suspend fun delete(id: ConversationId, requestOptions: RequestOptions?): Boolean = try {
        requester.perform<DeleteResponse> {
            it.delete {
                url(path = "${ApiPath.Conversations}/${id.id}")
                requestOptions(requestOptions)
            }
        }.deleted
    } catch (exception: OpenAIAPIException) {
        if (exception.statusCode == HttpStatusCode.NotFound.value) false else throw exception
    }

    override suspend fun createConversationItems(
        id: ConversationId,
        request: ConversationItemsRequest,
        include: List<String>?,
        requestOptions: RequestOptions?
    ): PaginatedList<ConversationItem> = requester.perform {
        it.post {
            url(path = "${ApiPath.Conversations}/${id.id}/items") {
                include?.forEach { value -> parameter("include[]", value) }
            }
            contentType(ContentType.Application.Json)
            setBody(request)
            requestOptions(requestOptions)
        }.body()
    }

    override suspend fun conversationItem(
        id: ConversationId,
        itemId: String,
        include: List<String>?,
        requestOptions: RequestOptions?
    ): ConversationItem? = getOrNull("${ApiPath.Conversations}/${id.id}/items/$itemId", include, requestOptions)

    override suspend fun deleteConversationItem(
        id: ConversationId,
        itemId: String,
        requestOptions: RequestOptions?
    ): Conversation = requester.perform {
        it.delete {
            url(path = "${ApiPath.Conversations}/${id.id}/items/$itemId")
            requestOptions(requestOptions)
        }.body()
    }

    override suspend fun conversationItems(
        id: ConversationId,
        limit: Int?,
        order: SortOrder?,
        after: String?,
        include: List<String>?,
        requestOptions: RequestOptions?
    ): PaginatedList<ConversationItem> = requester.perform {
        it.get {
            url(path = "${ApiPath.Conversations}/${id.id}/items") {
                limit?.let { value -> parameter("limit", value) }
                order?.let { value -> parameter("order", value.order) }
                after?.let { value -> parameter("after", value) }
                include?.forEach { value -> parameter("include[]", value) }
            }
            requestOptions(requestOptions)
        }.body()
    }

    private suspend inline fun <reified T : Any> getOrNull(
        path: String,
        include: List<String>?,
        requestOptions: RequestOptions?
    ): T? = try {
        requester.perform<T> {
            it.get {
                url(path = path) {
                    include?.forEach { value -> parameter("include[]", value) }
                }
                requestOptions(requestOptions)
            }
        }
    } catch (exception: OpenAIAPIException) {
        if (exception.statusCode == HttpStatusCode.NotFound.value) null else throw exception
    }
}
