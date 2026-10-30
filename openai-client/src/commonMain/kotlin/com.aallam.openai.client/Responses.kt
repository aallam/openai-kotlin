package com.aallam.openai.client

import com.aallam.openai.api.core.PaginatedList
import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.core.SortOrder
import com.aallam.openai.api.response.Response
import com.aallam.openai.api.response.ResponseId
import com.aallam.openai.api.response.ResponseInputItem
import com.aallam.openai.api.response.ResponseRequest
import com.aallam.openai.api.response.*
import kotlinx.coroutines.flow.Flow

/**
 * Create and manage model responses.
 */
public interface Responses {

    /**
     * Creates a model response.
     */
    public suspend fun response(
        request: ResponseRequest,
        requestOptions: RequestOptions? = null
    ): Response

    /**
     * Retrieves a response by its identifier.
     */
    public suspend fun response(
        id: ResponseId,
        requestOptions: RequestOptions? = null
    ): Response?

    /**
     * Deletes a stored response.
     */
    public suspend fun delete(
        id: ResponseId,
        requestOptions: RequestOptions? = null
    ): Boolean

    /**
     * Cancels an in-progress response.
     */
    public suspend fun cancel(
        id: ResponseId,
        requestOptions: RequestOptions? = null
    ): Response?

    /**
     * Lists input items for a response.
     */
    public suspend fun responseInputItems(
        id: ResponseId,
        limit: Int? = null,
        order: SortOrder? = null,
        after: String? = null,
        before: String? = null,
        requestOptions: RequestOptions? = null
    ): PaginatedList<ResponseInputItem>

    /** Creates a response with current request controls and complete result metadata. */
    public suspend fun createResponse(request: ResponseCreateRequest, requestOptions: RequestOptions? = null): ResponseResult =
        throw UnsupportedOperationException("Current response creation is not implemented")

    /** Retrieves a current response, optionally including additional tool or reasoning fields. */
    public suspend fun retrieveResponse(id: ResponseId, include: List<String> = emptyList(), requestOptions: RequestOptions? = null): ResponseResult? =
        throw UnsupportedOperationException("Current response retrieval is not implemented")

    /** Cancels a background response and returns its current result metadata. */
    public suspend fun cancelResponse(id: ResponseId, requestOptions: RequestOptions? = null): ResponseResult? =
        throw UnsupportedOperationException("Current response cancellation is not implemented")

    /** Lists current input items, optionally including tool or reasoning fields. */
    public suspend fun responseInputItemsPage(
        id: ResponseId,
        include: List<String> = emptyList(),
        limit: Int? = null,
        order: SortOrder? = null,
        after: String? = null,
        before: String? = null,
        requestOptions: RequestOptions? = null,
    ): PaginatedList<ResponseInputEntry> = throw UnsupportedOperationException("Current input item listing is not implemented")

    /** Streams a current response. Cancelling collection closes the HTTP connection. */
    public fun streamResponses(request: ResponseCreateRequest, requestOptions: RequestOptions? = null): Flow<ResponseStreamEvent> =
        throw UnsupportedOperationException("Response streaming is not implemented")

    /** Resumes events from a stored background response after a sequence number. */
    public fun resumeResponse(
        id: ResponseId,
        startingAfter: Int? = null,
        include: List<String>? = null,
        includeObfuscation: Boolean? = null,
        requestOptions: RequestOptions? = null,
    ): Flow<ResponseStreamEvent> = throw UnsupportedOperationException("Response streaming is not implemented")

    /** Compacts context into items suitable for a later response. */
    public suspend fun compactResponse(request: ResponseCompactRequest, requestOptions: RequestOptions? = null): ResponseCompaction =
        throw UnsupportedOperationException("Response compaction is not implemented")

    /** Counts input tokens without generating output. */
    public suspend fun responseInputTokens(request: ResponseTokenCountRequest, requestOptions: RequestOptions? = null): ResponseTokenCount =
        throw UnsupportedOperationException("Response token counting is not implemented")

    /**
     * Opens a scoped Responses WebSocket. The block owns the connection lifetime.
     * Requires a WebSocket-capable engine. secure=false is intended only for trusted local servers.
     */
    public suspend fun <T> responseSession(
        secure: Boolean = true,
        requestOptions: RequestOptions? = null,
        block: suspend ResponseConnection.() -> T,
    ): T = throw UnsupportedOperationException("Responses WebSockets are not implemented by this client")
}
