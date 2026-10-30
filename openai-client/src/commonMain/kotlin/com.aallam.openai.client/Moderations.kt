package com.aallam.openai.client

import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.moderation.ModerationRequest
import com.aallam.openai.api.moderation.MultimodalModerationRequest
import com.aallam.openai.api.moderation.TextModeration

/**
 * Classifies text and images against OpenAI's content policy.
 */
public interface Moderations {

    /**
     * Classifies if a text violates OpenAI's Content Policy.
     *
     * @param request moderation request.
     * @param requestOptions request options.
     */
    public suspend fun moderations(request: ModerationRequest, requestOptions: RequestOptions? = null): TextModeration

    /**
     * Classifies text and image content together using an omni-moderation model.
     *
     * @param request multimodal moderation request.
     * @param requestOptions request options.
     * @throws UnsupportedOperationException when a custom client has not implemented this overload.
     */
    public suspend fun moderate(
        request: MultimodalModerationRequest,
        requestOptions: RequestOptions? = null,
    ): TextModeration = throw UnsupportedOperationException("This client does not support multimodal moderation.")
}
