package com.aallam.openai.api.classification

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class Example(

    /**
     * The position of this document in the request list.
     */
    @SerialName("document") public val document: Int,

    /**
     * The category being classified.
     */
    @SerialName("label") public val label: String,

    /**
     * The classified text.
     */
    @SerialName("text") public val text: String
)
