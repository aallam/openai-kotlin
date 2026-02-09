package com.aallam.openai.api.chat

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * Configuration for web search in chat completions.
 */
@Serializable
public data class WebSearchOptions(
    /**
     * High-level context size to use for web search.
     */
    @SerialName("search_context_size") public val searchContextSize: SearchContextSize? = null,

    /**
     * Approximate location hint used by web search.
     */
    @SerialName("user_location") public val userLocation: UserLocation? = null,
)

/**
 * Search context size setting for web search.
 */
@JvmInline
@Serializable
public value class SearchContextSize(public val value: String) {
    public companion object {
        public val Low: SearchContextSize = SearchContextSize("low")
        public val Medium: SearchContextSize = SearchContextSize("medium")
        public val High: SearchContextSize = SearchContextSize("high")
    }
}

/**
 * Location hint used by web search.
 */
@Serializable
public data class UserLocation(
    /**
     * Location type. Defaults to "approximate".
     */
    @SerialName("type") public val type: String = "approximate",

    /**
     * Approximate location details.
     */
    @SerialName("approximate") public val approximate: ApproximateLocation? = null,
)

/**
 * Approximate location details.
 */
@Serializable
public data class ApproximateLocation(
    @SerialName("city") public val city: String? = null,
    @SerialName("country") public val country: String? = null,
    @SerialName("region") public val region: String? = null,
    @SerialName("timezone") public val timezone: String? = null,
)
