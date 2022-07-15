package com.aallam.openai.api.core

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * File status.
 */
@Serializable
@JvmInline
public value class Status(public val value: String) {
    public companion object {
        public val Succeeded: Status = Status("succeeded")
        public val Processed: Status = Status("processed")
        public val Deleted: Status = Status("deleted")
        public val Failed: Status = Status("failed")
        public val Cancelled: Status = Status("cancelled")
    }
}
