package com.aallam.openai.api.thread

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@JvmInline
@Serializable
public value class ThreadEvent(public val event: String) {

    public companion object {
        /**
         * Occurs when a new thread is created.
         */
        public val ThreadCreated: ThreadEvent = ThreadEvent("thread.created")

        /**
         * Occurs when a new run is created.
         */
        public val ThreadRunCreated: ThreadEvent = ThreadEvent("thread.run.created")

        /**
         * Occurs when a run moves to a queued status.
         */
        public val ThreadRunQueued: ThreadEvent = ThreadEvent("thread.run.queued")

        /**
         * Occurs when a run moves to an in_progress status.
         */
        public val ThreadRunInProgress: ThreadEvent = ThreadEvent("thread.run.in_progress")

        /**
         * Occurs when a run moves to a requires_action status.
         */
        public val ThreadRunRequiresAction: ThreadEvent = ThreadEvent("thread.run.requires_action")

        /**
         * Occurs when a run is completed.
         */
        public val ThreadRunCompleted: ThreadEvent = ThreadEvent("thread.run.completed")

        /**
         * Occurs when a run fails.
         */
        public val ThreadRunFailed: ThreadEvent = ThreadEvent("thread.run.failed")

        /**
         * Occurs when a run moves to a cancelling status.
         */
        public val ThreadRunCancelling: ThreadEvent = ThreadEvent("thread.run.cancelling")

        /**
         * Occurs when a run is cancelled.
         */
        public val ThreadRunCancelled: ThreadEvent = ThreadEvent("thread.run.cancelled")

        /**
         * Occurs when a run expires.
         */
        public val ThreadRunExpired: ThreadEvent = ThreadEvent("thread.run.expired")

        /**
         * Occurs when a run step is created.
         */
        public val ThreadRunStepCreated: ThreadEvent = ThreadEvent("thread.run.step.created")

        /**
         * Occurs when a run step moves to an in_progress state.
         */
        public val ThreadRunStepInProgress: ThreadEvent = ThreadEvent("thread.run.step.in_progress")

        /**
         * Occurs when parts of a run step are being streamed.
         */
        public val ThreadRunStepDelta: ThreadEvent = ThreadEvent("thread.run.step.delta")

        /**
         * Occurs when a run step is completed.
         */
        public val ThreadRunStepCompleted: ThreadEvent = ThreadEvent("thread.run.step.completed")

        /**
         * Occurs when a run step fails.
         */
        public val ThreadRunStepFailed: ThreadEvent = ThreadEvent("thread.run.step.failed")

        /**
         * Occurs when a run step is cancelled.
         */
        public val ThreadRunStepCancelled: ThreadEvent = ThreadEvent("thread.run.step.cancelled")

        /**
         * Occurs when a run step expires.
         */
        public val ThreadRunStepExpired: ThreadEvent = ThreadEvent("thread.run.step.expired")

        /**
         * Occurs when a message is created.
         */
        public val ThreadMessageCreated: ThreadEvent = ThreadEvent("thread.message.created")

        /**
         * Occurs when a message moves to an in_progress state.
         */
        public val ThreadMessageInProgress: ThreadEvent = ThreadEvent("thread.message.in_progress")

        /**
         * Occurs when parts of a Message are being streamed.
         */
        public val ThreadMessageDelta: ThreadEvent = ThreadEvent("thread.message.delta")

        /**
         * Occurs when a message is completed.
         */
        public val ThreadMessageCompleted: ThreadEvent = ThreadEvent("thread.message.completed")

        /**
         * Occurs when a message ends before it is completed.
         */
        public val ThreadMessageIncomplete: ThreadEvent = ThreadEvent("thread.message.incomplete")
    }
}
