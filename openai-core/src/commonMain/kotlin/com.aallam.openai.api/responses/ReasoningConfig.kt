package com.aallam.openai.api.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Configuration options for reasoning models
 */
@Serializable
public data class ReasoningConfig(
    /**
     * Constrains effort on reasoning for reasoning models.
     * Currently supported values are `low`, `medium`, and `high`.
     * Reducing reasoning effort can result in faster responses and fewer tokens used on reasoning in a response.
     */
    @SerialName("effort")
    val effort: ReasoningEffort? = null,

    /**
     * A summary of the reasoning performed by the model.
     * This can be useful for debugging and understanding the model's reasoning process.
     * One of `concise` or `detailed`.
     */
    @SerialName("generate_summary")
    val generateSummary: String? = null
)


/**
 * Reasoning effort levels for models with reasoning capabilities
 */
@Serializable
public enum class ReasoningEffort {
    /**
     * Low reasoning effort
     */
    @SerialName("low")
    LOW,

    /**
     * Medium reasoning effort (default)
     */
    @SerialName("medium")
    MEDIUM,

    /**
     * High reasoning effort
     */
    @SerialName("high")
    HIGH
}