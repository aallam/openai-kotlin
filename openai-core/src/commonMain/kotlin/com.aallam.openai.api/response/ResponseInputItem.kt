package com.aallam.openai.api.response

import com.aallam.openai.api.chat.ChatRole
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Input items for the responses API.
 */
@Serializable
public sealed interface ResponseInputItem {
    
    /**
     * A message input item.
     */
    @Serializable
    @SerialName("message")
    public data class Message(
        /**
         * The role of the message author.
         */
        @SerialName("role") public val role: ChatRole,

        /**
         * The content of the message.
         */
        @SerialName("content") public val content: String,

        /**
         * The status of the message. Required for assistant messages, always "completed".
         */
        @SerialName("status") public val status: String? = null,
    ) : ResponseInputItem

    /**
     * A reasoning input item containing previous reasoning traces.
     */
    @Serializable
    @SerialName("reasoning")
    public data class Reasoning(
        /**
         * The reasoning content from a previous response.
         */
        @SerialName("content") public val content: List<ReasoningContentPart>,

        /**
         * A summary of the reasoning content.
         */
        @SerialName("summary") public val summary: List<SummaryContentPart>,

        /**
         * The encrypted reasoning content from a previous response.
         */
        @SerialName("encrypted_content") public val encryptedContent: String? = null,
    ) : ResponseInputItem
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
