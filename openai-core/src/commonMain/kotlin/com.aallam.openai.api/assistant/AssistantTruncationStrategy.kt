package com.aallam.openai.api.assistant

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class AssistantTruncationStrategy(
    /**
     * The truncation strategy to use for the thread.
     * The default is auto.
     * If set to `last_messages`, the thread will be truncated to the n most recent messages in the thread.
     * When set to `auto`, messages in the middle of the thread will be dropped to fit the context length of the model, `max_prompt_tokens`.
     */
    @SerialName("type") val type: String,

    /**
     * The number of most recent messages from the thread when constructing the context for the run.
     */
    @SerialName("last_messages") val lastMessages: Int? = null
)
