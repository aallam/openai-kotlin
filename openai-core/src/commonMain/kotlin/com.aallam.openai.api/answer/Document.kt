package com.aallam.openai.api.answer

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
