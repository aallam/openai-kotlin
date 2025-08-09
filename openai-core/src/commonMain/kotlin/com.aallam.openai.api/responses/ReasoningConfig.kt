package com.aallam.openai.api.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

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
    @Deprecated("Use summary instead.")
    @SerialName("generate_summary")
    val generateSummary: ReasoningSummary? = null,

    /**
     * A summary of the reasoning performed by the model.
     * This can be useful for debugging and understanding the model's reasoning process.
     * One of `concise` or `detailed`.
     */
    @SerialName("summary")
    val summary: ReasoningSummary? = null
)


/**
 * Reasoning effort levels for models with reasoning capabilities
 */
@JvmInline
@Serializable
public value class ReasoningEffort(public val value: String) {
    public companion object {
        /**
         * Minimal reasoning effort
         */
        public val Minimal: ReasoningEffort = ReasoningEffort("minimal")

        /**
         * Low reasoning effort
         */
        public val Low: ReasoningEffort = ReasoningEffort("low")

        /**
         * Medium reasoning effort (default)
         */
        public val Medium: ReasoningEffort = ReasoningEffort("medium")

        /**
         * High reasoning effort
         */
        public val High: ReasoningEffort = ReasoningEffort("high")
    }
}

/**
 * Reasoning summary levels for models with reasoning capabilities
 */
@JvmInline
@Serializable
public value class ReasoningSummary(public val value: String) {
    public companion object {
        public val Auto: ReasoningSummary = ReasoningSummary("auto")
        public val Concise: ReasoningSummary = ReasoningSummary("concise")
        public val Detailed: ReasoningSummary = ReasoningSummary("detailed")
    }
}
