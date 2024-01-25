package com.aallam.openai.api.chat

import com.aallam.openai.api.core.Usage
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.azure.api.filtering.ContentFilterResultsForPrompt
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * An object containing a response from the chat stream completion api.
 *
 * [documentation](https://platform.openai.com/docs/api-reference/chat/create)
 */
@Serializable
public data class ChatCompletionChunk(
    /**
     * A unique id assigned to this completion
     */
    @SerialName("id")
    public val id: String,

    /**
     * The creation time in epoch milliseconds.
     */
    @SerialName("created")
    public val created: Int,

    /**
     * The model used.
     */
    @SerialName("model")
    public val model: ModelId,

    /**
     * A list of generated completions
     */
    @SerialName("choices")
    public val choices: List<ChatChunk>,

    /**
     * Text completion usage data.
     */
    @SerialName("usage")
    public val usage: Usage? = null,

    /**
     * Content filtering results for zero or more prompts in the request. In a streaming request,
     * results for different prompts may arrive at different times or in different orders.
     */
    @SerialName("prompt_filter_results")
    val promptFilterResults: List<ContentFilterResultsForPrompt>? = null
)
