package com.aallam.openai.api.core

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public class Usage(
    /**
     * Count of prompts tokens.
     */
    @SerialName("prompt_tokens") public val promptTokens: Int? = null,
    /**
     * Count of completion tokens.
     */
    @SerialName("completion_tokens") public val completionTokens: Int? = null,
    /**
     * Count of total tokens.
     */
    @SerialName("total_tokens") public val totalTokens: Int? = null,
)
