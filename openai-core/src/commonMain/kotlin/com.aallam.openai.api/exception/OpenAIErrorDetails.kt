package com.aallam.openai.api.exception

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents an error response from the OpenAI API.
 *
 * @param detail information about the error that occurred.
 */
@Serializable
public data class OpenAIError(
    @SerialName("error") public val detail: OpenAIErrorDetails?,
)

/**
 * Represents an error object returned by the OpenAI API.
 *
 * @param code error code returned by the OpenAI API.
 * @param message human-readable error message describing the error that occurred.
 * @param param the parameter that caused the error, if applicable.
 * @param type the type of error that occurred.
 */
@Serializable
public data class OpenAIErrorDetails(
    @SerialName("code") val code: String?,
    @SerialName("message") val message: String?,
    @SerialName("param") val param: String?,
    @SerialName("type") val type: String?,
)
