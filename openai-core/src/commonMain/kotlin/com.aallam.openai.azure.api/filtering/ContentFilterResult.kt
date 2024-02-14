package com.aallam.openai.azure.api.filtering

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Information about filtered content severity level and if it has been filtered or not.
 */
@Serializable
public data class ContentFilterResult(

    /**
     * Ratings for the intensity and risk level of filtered content.
     */
    @SerialName("severity")
    val severity: ContentFilterSeverity,

    /**
     * A value indicating whether the content has been filtered.
     */
    @SerialName("filtered")
    val filtered: Boolean
)
