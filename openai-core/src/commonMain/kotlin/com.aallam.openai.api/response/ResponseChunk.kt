package com.aallam.openai.api.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a chunk of a streaming response.
 */
@Serializable
public data class ResponseChunk(
    /**
     * The type of the streaming event.
     */
    @SerialName("type") public val type: String,
    
    /**
     * The sequence number of this chunk.
     */
    @SerialName("sequence_number") public val sequenceNumber: Int,
    
    /**
     * The response data (present in response.created, response.in_progress, response.completed events).
     */
    @SerialName("response") public val response: Response? = null,
    
    /**
     * The output index (present in output item events).
     */
    @SerialName("output_index") public val outputIndex: Int? = null,
    
    /**
     * The item data (present in response.output_item.added events).
     */
    @SerialName("item") public val item: ResponseOutputItem? = null,
    
    /**
     * The item ID (present in reasoning summary events).
     */
    @SerialName("item_id") public val itemId: String? = null,
    
    /**
     * The summary index (present in reasoning summary events).
     */
    @SerialName("summary_index") public val summaryIndex: Int? = null,
    
    /**
     * The summary part (present in response.reasoning_summary_part.added events).
     */
    @SerialName("part") public val part: SummaryContentPart? = null,
    
    /**
     * The text delta (present in delta events).
     */
    @SerialName("delta") public val delta: String? = null,
    
    /**
     * The obfuscation data (present in reasoning summary delta events).
     */
    @SerialName("obfuscation") public val obfuscation: String? = null,
    
    /**
     * The content index (present in message content events).
     */
    @SerialName("content_index") public val contentIndex: Int? = null,
)


