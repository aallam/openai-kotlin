package com.aallam.openai.api.responses

import kotlinx.serialization.Required
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * Web search context size
 */
@JvmInline
@Serializable
public value class WebSearchContextSize(public val value: String) {
    public companion object {
        /**
         * Low context size
         */
        public val Low: WebSearchContextSize = WebSearchContextSize("low")

        /**
         * Medium context size
         */
        public val Medium: WebSearchContextSize = WebSearchContextSize("medium")

        /**
         * High context size
         */
        public val High: WebSearchContextSize = WebSearchContextSize("high")
    }
}

/**
 * Web search location
 */
@Serializable
public data class WebSearchLocation(
    /**
     * Free text input for the city of the user, e.g., San Francisco.
     */
    @SerialName("city")
    val city: String? = null,

    /**
     * The two-letter ISO-country code of the user, e.g., US.
     */
    @SerialName("country")
    val country: String? = null,

    /**
     * Free text input for the region of the user, e.g., California.
     */
    @SerialName("region")
    val region: String? = null,

    /**
     * The IANA time zone of the user, e.g., America/Los_Angeles.
     */
    @SerialName("timezone")
    val timezone: String? = null,

    ) {
    /**
     * The type of location approximation. Always approximate.
     */
    @SerialName("type")
    @Required
    val type: String = "approximate"
}

/**
 * Web search tool call in a response
 */
@Serializable
@SerialName("web_search_call")
public data class WebSearchToolCall(
    /**
     * The unique ID of the web search tool call.
     */
    @SerialName("id")
    override val id: String,

    /**
     * The status of the web search tool call.
     */
    @SerialName("status")
    val status: ResponseStatus
) : ResponseOutput
