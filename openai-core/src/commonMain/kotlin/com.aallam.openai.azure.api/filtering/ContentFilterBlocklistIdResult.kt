package com.aallam.openai.azure.api.filtering

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName


/**
 * Represents the outcome of an evaluation against a custom blocklist as performed by content filtering.
 */
@Serializable
public data class ContentFilterBlocklistIdResult(

    /**
     * The ID of the custom blocklist evaluated.
     */
    @SerialName("id")
    val id: String,

    /**
     * A value indicating whether the content has been filtered.
     */
    @SerialName("filtered")
    val filtered: Boolean
)
