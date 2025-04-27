package com.aallam.openai.api.chat

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@Serializable
public data class WebSearchOptions(
    @SerialName("search_context_size")
    val searchContextSize: SearchContextSize? = null,
    @SerialName("user_location")
    val userLocation: UserLocation? = null
)

/**
 * High-level guidance for the amount of context window space to use for the search.
 * One of low, medium, or high. `medium` is the default.
 */
@Serializable
@JvmInline
public value class SearchContextSize(public val id: String)

@Serializable
public data class UserLocation(
    val approximate: UserLocationApproximate? = null,
    val type: String = "approximate" // The type of location approximation. Always approximate.
)

@Serializable
public data class UserLocationApproximate(
    val city: String? = null,
    val country: String? = null,
    val region: String? = null,
    val timezone: String? = null
)

/**
 * Create a new [WebSearchOptions] instance.
 */
public fun webSearchOptions(block: WebSearchOptionsBuilder.() -> Unit): WebSearchOptions {
    return WebSearchOptionsBuilder().apply(block).build()
}

/**
 * Builder for [WebSearchOptions].
 */
public class WebSearchOptionsBuilder {
    /**
     * Possible values: low, medium, high. `medium` is the default
     */
    public var searchContextSize: SearchContextSize? = null

    public var userLocation: UserLocation? = null

    /**
     * Build the [WebSearchOptions] instance.
     */
    public fun build(): WebSearchOptions = WebSearchOptions(
        searchContextSize = searchContextSize,
        userLocation = userLocation
    )
}