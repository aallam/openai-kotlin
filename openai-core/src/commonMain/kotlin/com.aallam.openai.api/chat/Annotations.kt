package com.aallam.openai.api.chat

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class Annotations(
    /**
     * Type of annotation. e.g. `url_citation`
     */
    val type: String,

    /**
     * An annotation object will contain the URL and title of the cited source,
     * as well as the start and end index characters in the model's response where those sources were used.
     */
    @SerialName("url_citation")
    val urlCitation: UrlCitation? = null
)

@Serializable
public data class UrlCitation(
    /**
     * Start index of characters in the model's response
     */
    @SerialName("start_index")
    val startIndex: Int,
    /**
     * End index of characters in the model's response
     */
    @SerialName("end_index")
    val endIndex: Int,
    /**
     * URL of the cited source
     */
    val url: String,
    /**
     * Page title of the cited source
     */
    val title: String? = null,
)