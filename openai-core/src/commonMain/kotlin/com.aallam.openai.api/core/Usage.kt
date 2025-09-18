package com.aallam.openai.api.core

import kotlinx.serialization.*

@Serializable
public data class Usage(
    /**
     * Count of prompt tokens (Chat Completions API).
     */
    @SerialName("prompt_tokens")
    public val promptTokens: Int? = null,

    /**
     * Count of completion tokens (Chat Completions API).
     */
    @SerialName("completion_tokens")
    public val completionTokens: Int? = null,

    /**
     * Count of total tokens.
     */
    @SerialName("total_tokens")
    public val totalTokens: Int? = null,

    /**
     * Count of input tokens (Responses API).
     */
    @SerialName("input_tokens")
    public val inputTokens: Int? = null,

    /**
     * Count of output tokens (Responses API).
     */
    @SerialName("output_tokens")
    public val outputTokens: Int? = null,

    /**
     * Details about prompt tokens (Chat Completions API).
     */
    @SerialName("prompt_tokens_details")
    public val promptTokensDetails: PromptTokensDetails? = null,

    /**
     * Details about completion tokens (Chat Completions API).
     */
    @SerialName("completion_tokens_details")
    public val completionTokensDetails: CompletionTokensDetails? = null,

    /**
     * Details about input tokens (Responses API).
     */
    @SerialName("input_tokens_details")
    public val inputTokensDetails: InputTokensDetails? = null,

    /**
     * Details about output tokens (Responses API).
     */
    @SerialName("output_tokens_details")
    public val outputTokensDetails: OutputTokensDetails? = null,
)

/**
 * Details about prompt tokens (Chat Completions API).
 */
@Serializable
public data class PromptTokensDetails(
    /**
     * Number of cached tokens.
     */
    @SerialName("cached_tokens")
    public val cachedTokens: Int? = null,

    /**
     * Number of audio tokens.
     */
    @SerialName("audio_tokens")
    public val audioTokens: Int? = null,
)

/**
 * Details about completion tokens (Chat Completions API).
 */
@Serializable
public data class CompletionTokensDetails(
    /**
     * Number of reasoning tokens.
     */
    @SerialName("reasoning_tokens")
    public val reasoningTokens: Int? = null,

    /**
     * Number of audio tokens.
     */
    @SerialName("audio_tokens")
    public val audioTokens: Int? = null,

    /**
     * Number of accepted prediction tokens.
     */
    @SerialName("accepted_prediction_tokens")
    public val acceptedPredictionTokens: Int? = null,

    /**
     * Number of rejected prediction tokens.
     */
    @SerialName("rejected_prediction_tokens")
    public val rejectedPredictionTokens: Int? = null,
)

/**
 * Details about input tokens (Responses API).
 */
@Serializable
public data class InputTokensDetails(
    /**
     * Number of cached tokens.
     */
    @SerialName("cached_tokens")
    public val cachedTokens: Int? = null,
)

/**
 * Details about output tokens (Responses API).
 */
@Serializable
public data class OutputTokensDetails(
    /**
     * Number of reasoning tokens.
     */
    @SerialName("reasoning_tokens")
    public val reasoningTokens: Int? = null,
)
