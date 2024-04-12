package com.aallam.openai.api.core

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * Represents a stream event type.
 */
@JvmInline
@Serializable
public value class EventType(public val value: String) {

    public companion object {
        /**
         * Occurs when a new thread is created.
         */
        public val ThreadCreated: EventType = EventType("thread.created")

        /**
         * Occurs when a new run is created.
         */
        public val ThreadRunCreated: EventType = EventType("thread.run.created")

        /**
         * Occurs when a run moves to a queued status.
         */
        public val ThreadRunQueued: EventType = EventType("thread.run.queued")

        /**
         * Occurs when a run moves to an in_progress status.
         */
        public val ThreadRunInProgress: EventType = EventType("thread.run.in_progress")

        /**
         * Occurs when a run moves to a requires_action status.
         */
        public val ThreadRunRequiresAction: EventType = EventType("thread.run.requires_action")

        /**
         * Occurs when a run is completed.
         */
        public val ThreadRunCompleted: EventType = EventType("thread.run.completed")

        /**
         * Occurs when a run fails.
         */
        public val ThreadRunFailed: EventType = EventType("thread.run.failed")

        /**
         * Occurs when a run moves to a cancelling status.
         */
        public val ThreadRunCancelling: EventType = EventType("thread.run.cancelling")

        /**
         * Occurs when a run is cancelled.
         */
        public val ThreadRunCancelled: EventType = EventType("thread.run.cancelled")

        /**
         * Occurs when a run expires.
         */
        public val ThreadRunExpired: EventType = EventType("thread.run.expired")

        /**
         * Occurs when a run step is created.
         */
        public val ThreadRunStepCreated: EventType = EventType("thread.run.step.created")

        /**
         * Occurs when a run step moves to an in_progress state.
         */
        public val ThreadRunStepInProgress: EventType = EventType("thread.run.step.in_progress")

        /**
         * Occurs when parts of a run step are being streamed.
         */
        public val ThreadRunStepDelta: EventType = EventType("thread.run.step.delta")

        /**
         * Occurs when a run step is completed.
         */
        public val ThreadRunStepCompleted: EventType = EventType("thread.run.step.completed")

        /**
         * Occurs when a run step fails.
         */
        public val ThreadRunStepFailed: EventType = EventType("thread.run.step.failed")

        /**
         * Occurs when a run step is cancelled.
         */
        public val ThreadRunStepCancelled: EventType = EventType("thread.run.step.cancelled")

        /**
         * Occurs when a run step expires.
         */
        public val ThreadRunStepExpired: EventType = EventType("thread.run.step.expired")

        /**
         * Occurs when a message is created.
         */
        public val ThreadMessageCreated: EventType = EventType("thread.message.created")

        /**
         * Occurs when a message moves to an in_progress state.
         */
        public val ThreadMessageInProgress: EventType = EventType("thread.message.in_progress")

        /**
         * Occurs when parts of a Message are being streamed.
         */
        public val ThreadMessageDelta: EventType = EventType("thread.message.delta")

        /**
         * Occurs when a message is completed.
         */
        public val ThreadMessageCompleted: EventType = EventType("thread.message.completed")

        /**
         * Occurs when a message ends before it is completed.
         */
        public val ThreadMessageIncomplete: EventType = EventType("thread.message.incomplete")

        /**
         * Occurs when an error occurs. This can happen due to an internal server error or a timeout.
         */
        public val Error: EventType = EventType("error")

        /**
         * Occurs when a stream ends.
         */
        public val Done: EventType = EventType("[DONE]")
    }
}
