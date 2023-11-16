package com.aallam.openai.api.core

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * Operation status.
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
        public val ValidatingFiles: Status = Status("validating_files")
        public val Queued: Status = Status("queued")
        public val Running: Status = Status("running")
        public val InProgress: Status = Status("in_progress")
        public val RequiresAction: Status = Status("requires_action")
        public val Cancelling: Status = Status("cancelling")
        public val Completed: Status = Status("completed")
        public val Expired: Status = Status("expired")
    }
}
