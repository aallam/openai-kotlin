package com.aallam.openai.api.exception


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class OpenAIAPIError(
    @SerialName("error")
    val error: Error?
)