package com.aallam.openai.api.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Status of an output item
 */
@Serializable
public enum class ResponseStatus {
    @SerialName("in_progress")
    IN_PROGRESS,

    @SerialName("completed")
    COMPLETED,

    @SerialName("incomplete")
    INCOMPLETE
}