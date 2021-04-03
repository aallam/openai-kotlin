package com.aallam.openai.api.classification

import com.aallam.openai.api.ExperimentalOpenAI
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@ExperimentalOpenAI
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
