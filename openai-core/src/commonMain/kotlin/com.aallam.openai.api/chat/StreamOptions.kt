package com.aallam.openai.api.chat

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Options for streaming response.
 */
@Serializable
public data class StreamOptions(
    /**
     * If set, an additional chunk will be streamed before the data: `[DONE]` message.
     * The usage field on this chunk shows the token usage statistics for the entire request, and the choices field will
     * always be an empty array. All other chunks will also include a usage field, but with a null value.
     */
    @SerialName("include_usage") public val includeUsage: Boolean? = null,
)

/**
 * Create a new [StreamOptions] instance.
 */
public fun streamOptions(block: StreamOptionsBuilder.() -> Unit): StreamOptions {
    return StreamOptionsBuilder().apply(block).build()
}

/**
 * Builder for [StreamOptions].
 */
public class StreamOptionsBuilder {

    /**
     * If set, an additional chunk will be streamed before the data: `[DONE]` message.
     * The usage field on this chunk shows the token usage statistics for the entire request, and the choices field will
     * always be an empty array. All other chunks will also include a usage field, but with a null value.
     */
    public var includeUsage: Boolean? = null

    /**
     * Build the [StreamOptions] instance.
     */
    public fun build(): StreamOptions = StreamOptions(
        includeUsage = includeUsage,
    )
}
