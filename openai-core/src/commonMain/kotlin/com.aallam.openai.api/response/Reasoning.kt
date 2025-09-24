package com.aallam.openai.api.response

import com.aallam.openai.api.OpenAIDsl
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A reasoning output item containing reasoning traces.
 */
@Serializable
@SerialName("reasoning")
public data class Reasoning(
    /**
     * Unique identifier for this output item.
     */
    @SerialName("id") public val id: String,

    /**
     * The encrypted reasoning content.
     */
    @SerialName("encrypted_content") public val encryptedContent: String? = null,

    /**
     * The reasoning summary.
     */
    @SerialName("summary") public val summary: List<SummaryContentPart>,

    /**
     * Reasoning text content.
     */
    @SerialName("content") public val content: List<ReasoningContentPart>? = null,

    /**
     * The status of the reasoning.
     */
    @SerialName("status") public val status: ReasoningStatus? = null,
) : ResponseOutputItem, ResponseInputItem

@Serializable
public enum class ReasoningStatus {
    @SerialName("completed")
    Completed,

    @SerialName("in_progress")
    InProgress,

    @SerialName("incomplete")
    Incomplete,
}

/**
 * Content parts for reasoning summaries.
 */
@Serializable
public sealed interface SummaryContentPart

/**
 * Summary text content part.
 *
 * @param text the text content.
 */
@Serializable
@SerialName("summary_text")
public data class SummaryTextPart(@SerialName("text") val text: String) : SummaryContentPart

/**
 * Output text content part (used in streaming responses).
 *
 * @param text the text content.
 */
@Serializable
@SerialName("output_text")
public data class OutputTextPart(@SerialName("text") val text: String) : SummaryContentPart

/**
 * Content parts for reasoning content.
 */
@Serializable
public sealed interface ReasoningContentPart

/**
 * Reasoning text content part.
 *
 * @param text the text content.
 */
@Serializable
@SerialName("reasoning_text")
public data class ReasoningTextPart(@SerialName("text") val text: String) : ReasoningContentPart

/**
 * Reasoning encrypted content part.
 *
 * @param encryptedContent the encrypted reasoning content.
 */
@Serializable
@SerialName("reasoning_encrypted")
public data class ReasoningEncryptedPart(@SerialName("encrypted_content") val encryptedContent: String) : ReasoningContentPart


/**
 * Builder for reasoning input items.
 */
@OpenAIDsl
public class ReasoningBuilder {
    private val content = mutableListOf<ReasoningContentPart>()
    private val summary = mutableListOf<SummaryContentPart>()

    public var id: String? = null

    public var status: ReasoningStatus? = null

    /**
     * The encrypted reasoning content from a previous response.
     */
    public var encryptedContent: String? = null

    /**
     * Add reasoning text content.
     */
    public fun text(text: String) {
        content.add(ReasoningTextPart(text))
    }

    /**
     * Add summary text content.
     */
    public fun summaryText(text: String) {
        summary.add(SummaryTextPart(text))
    }

    /**
     * Add output text content.
     */
    public fun outputText(text: String) {
        summary.add(OutputTextPart(text))
    }

    internal fun build(): Reasoning = Reasoning(
        id = requireNotNull(id),
        encryptedContent = encryptedContent,
        status = status,
        content = content.toList(),
        summary = summary.toList(),
    )
}