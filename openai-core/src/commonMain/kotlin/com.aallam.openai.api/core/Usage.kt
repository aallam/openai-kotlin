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
)

/** Breakdown of prompt token usage. */
@Serializable
public data class PromptTokensDetails(
    /** Tokens served from the prompt cache. */
    @SerialName("cached_tokens") public val cachedTokens: Int? = null,
    /** Audio input tokens. */
    @SerialName("audio_tokens") public val audioTokens: Int? = null,
)

/** Breakdown of generated token usage. */
@Serializable
public data class CompletionTokensDetails(
    /** Reasoning tokens. */
    @SerialName("reasoning_tokens") public val reasoningTokens: Int? = null,
    /** Audio output tokens. */
    @SerialName("audio_tokens") public val audioTokens: Int? = null,
    /** Predicted tokens accepted in the output. */
    @SerialName("accepted_prediction_tokens") public val acceptedPredictionTokens: Int? = null,
    /** Predicted tokens rejected during generation. */
    @SerialName("rejected_prediction_tokens") public val rejectedPredictionTokens: Int? = null,
)
