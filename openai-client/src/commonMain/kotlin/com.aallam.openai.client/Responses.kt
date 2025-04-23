package com.aallam.openai.client

import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.responses.Response
import com.aallam.openai.api.responses.ResponseIncludable
import com.aallam.openai.api.responses.ResponseItem
import com.aallam.openai.api.responses.ResponseRequest

/** Interface for OpenAI's Responses API */
public interface Responses {
    /**
     * Create a new response.
     *
     * @param request The request for creating a response
     * @param requestOptions Optional request configuration
     * @return The created response
     */
    public suspend fun createResponse(
            request: ResponseRequest,
            requestOptions: RequestOptions? = null
    ): Response

    /**
     * Retrieves a model response with the given ID.
     *
     * @param responseId The ID of the response to retrieve
     * @param include Additional fields to include in the response.
     * @param requestOptions Optional request configuration
     */
    public suspend fun getResponse(
            responseId: String,
            include: List<ResponseIncludable>? = null,
            requestOptions: RequestOptions? = null): Response

    /**
     * Deletes a model response with the given ID.
     *
     * @param responseId The ID of the response to delete
     * @param requestOptions Optional request configuration
     */
    public suspend fun deleteResponse(
            responseId: String,
            requestOptions: RequestOptions? = null): Boolean

    /**
     * Returns a list of input items for a given response.
     *
     * @param responseId The ID of the response
     * @param after An item ID to list items after, used in pagination.
     * @param before An item ID to list items before, used in pagination.
     * @param include Additional fields to include in the response.
     * @param limit A limit on the number of objects to be returned. Limit can range between 1 and 100, and the default is 20.
     * @param order The order to return the input items in. Can be either "asc" or "desc". Default is "desc".
     * @param requestOptions Optional request configuration
     */
    public suspend fun listResponseItems(
            responseId: String,
            after: String? = null,
            before: String? = null,
            include: List<ResponseIncludable>? = null,
            limit: Int? = null,
            order: String? = null,
            requestOptions: RequestOptions? = null): List<ResponseItem>

    //TODO Streaming
}
