package com.aallam.openai.api.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

/**
 * A server-sent event from a streaming response.
 */
@Serializable
public data class ResponseStreamEvent(
    /** The event type (e.g. "response.created", "response.output_text.delta", "response.completed"). */
    @SerialName("type") public val type: String,

    /** Sequence number for ordering events. */
    @SerialName("sequence_number") public val sequenceNumber: Int? = null,

    /** Full response object, present on response lifecycle events (created, in_progress, completed, failed, incomplete). */
    @SerialName("response") public val response: Response? = null,

    /** Index of the output item this event relates to. */
    @SerialName("output_index") public val outputIndex: Int? = null,

    /** The output item, present on output_item.added / output_item.done events. */
    @SerialName("item") public val item: ResponseOutputItem? = null,

    /** ID of the output item this event relates to. */
    @SerialName("item_id") public val itemId: String? = null,

    /** Index of the content part within the output item. */
    @SerialName("content_index") public val contentIndex: Int? = null,

    /** The content part, present on content_part.added / content_part.done events. */
    @SerialName("part") public val part: JsonElement? = null,

    /** Text delta for text/refusal/reasoning/argument streaming events. */
    @SerialName("delta") public val delta: String? = null,

    /** Annotation added to output text. */
    @SerialName("annotation") public val annotation: JsonElement? = null,

    /** Summary index for reasoning summary events. */
    @SerialName("summary_index") public val summaryIndex: Int? = null,

    /** Obfuscation data for side-channel mitigation. */
    @SerialName("obfuscation") public val obfuscation: String? = null,

    /** Usage data, present on response.completed. */
    @SerialName("usage") public val usage: ResponseUsage? = null,
)
