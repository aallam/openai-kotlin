package com.aallam.openai.api.responses

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * Status of an output item
 */
@JvmInline
@Serializable
public value class ResponseStatus(public val value: String) {
    public companion object {
        public val InProgress: ResponseStatus = ResponseStatus("in_progress")
        public val Completed: ResponseStatus = ResponseStatus("completed")
        public val Incomplete: ResponseStatus = ResponseStatus("incomplete")
    }
}
