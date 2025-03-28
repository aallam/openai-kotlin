package com.aallam.openai.client

import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.responses.Response
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

    //TODO Streaming
}
