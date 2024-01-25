package com.aallam.openai.azure.api.filtering

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents the outcome of a detection operation against protected resources as performed by content filtering.
 */
@Serializable
public data class ContentFilterCitedDetectionResult(

    /**
     * A value indicating whether or not the content has been filtered.
     */
    @SerialName("filtered")
    val filtered: Boolean,

    /**
     * A value indicating whether detection occurred, irrespective of severity or whether the content was filtered.
     */
    @SerialName("detected")
    val detected: Boolean,

    /**
     * The internet location associated with the detection.
     */
    @SerialName("URL")
    val url: String,

    /**
     * The license description associated with the detection.
     */
    @SerialName("license")
    val license: String
)