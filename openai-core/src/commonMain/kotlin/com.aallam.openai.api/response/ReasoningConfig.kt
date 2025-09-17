package com.aallam.openai.api.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Configuration for reasoning behavior in responses.
 */
@Serializable
public data class ReasoningConfig(
    /**
     * Controls the effort level for reasoning.
     * Supported values: "low", "medium", "high"
     */
    @SerialName("effort") public val effort: String? = null,

    /**
     * Controls the summary generation for reasoning.
     * Supported values: "auto", "concise", "detailed"
     */
    @SerialName("summary") public val summary: String? = null,
)

/**
 * Reasoning trace containing the model's reasoning process.
 */
@Serializable
public data class ReasoningTrace(
    /**
     * The raw reasoning content from the model.
     */
    @SerialName("content") public val content: String? = null,
    
    /**
     * Encrypted reasoning content for stateless mode (future use).
     */
    @SerialName("encrypted_content") public val encryptedContent: String? = null,
)
