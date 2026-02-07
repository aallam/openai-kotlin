package com.aallam.openai.client

import com.aallam.openai.api.core.PaginatedList
import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.core.SortOrder
import com.aallam.openai.api.response.Response
import com.aallam.openai.api.response.ResponseId
import com.aallam.openai.api.response.ResponseInputItem
import com.aallam.openai.api.response.ResponseRequest

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
}
