package com.aallam.openai.client

import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.response.Response
import com.aallam.openai.api.response.ResponseChunk
import com.aallam.openai.api.response.ResponseRequest
import kotlinx.coroutines.flow.Flow

/**
 * The Responses API provides a stateless interface for generating responses with reasoning support.
 * This API is particularly useful for accessing reasoning traces from reasoning models.
 */
public interface Responses {

    /**
     * Creates a response for the given input.
     *
     * This method always operates in stateless mode (store=false), requiring manual context management.
     * To access reasoning traces, include "reasoning.encrypted_content" in the request's include parameter.
     *
     * @param request the response request containing model, input, and configuration
     * @param requestOptions additional request options
     * @return the generated response with optional reasoning traces
     */
    public suspend fun createResponse(
        request: ResponseRequest,
        requestOptions: RequestOptions? = null
    ): Response

    /**
     * Stream variant of [createResponse].
     *
     * This method streams response chunks as they are generated, allowing real-time access to
     * reasoning summaries and message content as they are produced.
     *
     * @param request the response request containing model, input, and configuration (with stream=true)
     * @param requestOptions additional request options
     * @return a flow of response chunks
     */
    public fun createResponseStream(
        request: ResponseRequest,
        requestOptions: RequestOptions? = null
    ): Flow<ResponseChunk>
}
