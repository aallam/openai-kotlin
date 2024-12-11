package com.aallam.openai.api.core

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class Usage(
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

    @SerialName("completion_tokens_details")
    val completionTokenDetails: CompletionTokenUsageDetails? = null,

    @SerialName("prompt_tokens_details")
    val promptTokenDetails: PromptTokenUsageDetails? = null
)

@Serializable
public data class PromptTokenUsageDetails(
    @SerialName("audio_tokens") public val audioTokens: Int? = null,
    @SerialName("cached_tokens") public val cachedTokens: Int? = null,
)

@Serializable
public data class CompletionTokenUsageDetails(
    @SerialName("audio_tokens") public val audioTokens: Int? = null,
    @SerialName("reasoning_tokens") public val reasoningTokens: Int? = null,
    @SerialName("accepted_prediction_tokens") public val acceptedPredictionTokens: Int? = null,
    @SerialName("rejected_prediction_tokens") public val rejectedPredictionTokens: Int? = null,
)
