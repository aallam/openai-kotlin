package com.aallam.openai.azure.api.core

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

/**
 * This class represents the error details of an HTTP response.
 */
@Serializable
public data class ResponseError(

    /**
     * The error code of this error.
     */
    @SerialName("code")
    val code: String,

    /**
     * The error message of this error.
     */
    @SerialName("message")
    val message: String,

    /**
     * The target of this error.
     */
    @SerialName("target")
    val target: String? = null,

    /**
     * The inner error information for this error.
     */
    @SerialName("innererror")
    val innerError: ResponseInnerError? = null,

    /**
     * A list of details about specific errors that led to this reported error.
     */
    @SerialName("details")
    val errorDetails: List<ResponseError>? = null
)