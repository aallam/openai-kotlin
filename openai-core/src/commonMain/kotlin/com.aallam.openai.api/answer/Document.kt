package com.aallam.openai.api.answer

import com.aallam.openai.api.ExperimentalOpenAI
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@ExperimentalOpenAI
@Serializable
public data class Document(

    /**
     * The position of this document in the request list.
     */
    @SerialName("document") public val document: Int,

    /**
     * Selected document text.
     */
    @SerialName("text") public val text: String
)
