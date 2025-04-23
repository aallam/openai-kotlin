package com.aallam.openai.api.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Information about an error during response generation
 */
@Serializable
public data class ResponseError(
    /**
     * The error code for the response.
     */
    @SerialName("code")
    val code: String? = null,

    /**
     * A human-readable description of the error.
     */
    @SerialName("message")
    val message: String? = null,
)
