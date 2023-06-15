package com.aallam.openai.api.chat

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class FunctionDescription(
    /**
     * The name of the function to be called. Must be a-z, A-Z, 0-9, or contain underscores and dashes, with a maximum
     * length of 64.
     */
    @SerialName("name") val name: String,
    /**
     * The description of what the function does.
     */
    @SerialName("description") val description: String? = null,
    /**
     * The parameters the functions accepts, described as a JSON Schema object. See the guide for examples and the
     * JSON Schema reference for documentation about the format.
     */
    @SerialName("parameters") val parameters: JsonData? = null,
)
