package com.aallam.openai.azure.api.filtering

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

/**
 * Represents the outcome of a detection operation performed by content filtering.
 */
@Serializable
public data class ContentFilterDetectionResult(

    /**
     * A value indicating whether the content has been filtered.
     */
    @SerialName("filtered")
    val filtered: Boolean,

    /**
     * A value indicating whether detection occurred, irrespective of severity or whether the content was filtered.
     */
    @SerialName("detected")
    val detected: Boolean
)
