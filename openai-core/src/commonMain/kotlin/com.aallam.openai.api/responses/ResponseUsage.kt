package com.aallam.openai.api.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents token usage details including input tokens, output tokens,
 * a breakdown of output tokens, and the total tokens used.
 */
@Serializable
public data class ResponseUsage(
    /**
     * The number of input tokens.
     */
    @SerialName("input_tokens")
    val inputTokens: Int,

    /**
     * A detailed breakdown of the input tokens.
     */
    @SerialName("input_tokens_details")
    val inputTokensDetails: InputTokensDetails,

    /**
     * The number of output tokens.
     */
    @SerialName("output_tokens")
    val outputTokens: Int,

    /**
     * A detailed breakdown of the output tokens.
     */
    @SerialName("output_tokens_details")
    val outputTokensDetails: OutputTokensDetails,

    /**
     * The total number of tokens used.
     */
    @SerialName("total_tokens")
    val totalTokens: Int
)

/**
 * A detailed breakdown of the input tokens.
 */
@Serializable
public data class InputTokensDetails(
    /**
     * The number of tokens that were retrieved from the cache.
     */
    @SerialName("cached_tokens")
    val cachedTokens: Int
)

/**
 * A detailed breakdown of the output tokens.
 */
@Serializable
public data class OutputTokensDetails(
    /**
     * The number of reasoning tokens.
     */
    @SerialName("reasoning_tokens")
    val reasoningTokens: Int
) 