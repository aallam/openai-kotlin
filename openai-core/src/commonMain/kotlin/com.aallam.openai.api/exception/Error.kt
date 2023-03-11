package com.aallam.openai.api.exception


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class Error(
    @SerialName("code")
    val code: String?,
    @SerialName("message")
    val message: String?,
    @SerialName("param")
    val `param`: String?,
    @SerialName("type")
    val type: String?
)