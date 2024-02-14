package com.aallam.openai.azure.api.core

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName


/**
 * The inner error of a ResponseError.
 */
@Serializable
public data class ResponseInnerError(

    /**
     * The error code of the inner error.
     */
    @SerialName("code")
    val code: String? = null,

    /**
     * The nested inner error for this error.
     */
    @SerialName("innererror")
    val innerError: ResponseInnerError? = null
)
