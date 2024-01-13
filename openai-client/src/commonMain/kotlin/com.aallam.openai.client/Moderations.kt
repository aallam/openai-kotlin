package com.aallam.openai.client

import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.moderation.ModerationRequest
import com.aallam.openai.api.moderation.TextModeration

/**
 * Given an input text, outputs if the model classifies it as violating OpenAI's content policy.
 */
public interface Moderations {

    /**
     * Classifies if a text violates OpenAI's Content Policy.
     *
     * @param request moderation request.
     * @param requestOptions request options.
     */
    public suspend fun moderations(request: ModerationRequest, requestOptions: RequestOptions? = null): TextModeration
}
