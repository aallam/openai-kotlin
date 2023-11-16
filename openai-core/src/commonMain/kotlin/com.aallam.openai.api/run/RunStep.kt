package com.aallam.openai.api.run

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.assistant.AssistantId
import com.aallam.openai.api.core.Status
import com.aallam.openai.api.run.internal.RunStepSerializer
import com.aallam.openai.api.thread.ThreadId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@BetaOpenAI
@Serializable(with = RunStepSerializer::class)
public sealed interface RunStep {
    /**
     * The identifier of the run step, which can be referenced in API endpoints.
     */
    public val id: RunStepId

    /**
     * The Unix timestamp (in seconds) for when the run step was created.
     */
    public val createdAt: Int

    /**
     * The ID of the assistant associated with the run step.
     */
    public val assistantId: AssistantId

    /**
     * The ID of the thread that was run.
     */
    public val threadId: ThreadId

    /**
     * The ID of the run that this run step is a part of.
     */
    public val runId: RunId

    /**
     * The status of the run step, which can be either [Status.InProgress], [Status.Cancelled], [Status.Failed],
     * [Status.Completed], or [Status.Expired].
     */
    public val status: Status

    /**
     * The details of the run step.
     */
    public val stepDetails: RunStepDetails

    /**
     * The last error associated with this run step.
     * Will be null if there are no errors.
     */
    public val lastError: LastError?

    /**
     * The Unix timestamp (in seconds) for when the run step expired.
     * A step is considered expired if the parent run is expired.
     */
    public val expiredAt: Int?

    /**
     * The Unix timestamp (in seconds) for when the run step was [Status.Cancelled].
     */
    public val cancelledAt: Int?

    /**
     * The Unix timestamp (in seconds) for when the run step failed.
     */
    public val failedAt: Int?

    /**
     * The Unix timestamp (in seconds) for when the run step completed.
     */
    public val completedAt: Int?

    /**
     * Set of 16 key-value pairs that can be attached to an object.
     * This can be useful for storing additional information about the object in a structured format.
     * Keys can be a maximum of 64 characters long, and values can be a maximum of 512 characters long.
     */
    public val metadata: Map<String, String>?
}

@BetaOpenAI
@Serializable
public data class MessageCreationStep(
    /**
     * The identifier of the run step, which can be referenced in API endpoints.
     */
    @SerialName("id") override val id: RunStepId,

    /**
     * The Unix timestamp (in seconds) for when the run step was created.
     */
    @SerialName("created_at") override val createdAt: Int,

    /**
     * The ID of the assistant associated with the run step.
     */
    @SerialName("assistant_id") override val assistantId: AssistantId,

    /**
     * The ID of the thread that was run.
     */
    @SerialName("thread_id") override val threadId: ThreadId,

    /**
     * The ID of the run that this run step is a part of.
     */
    @SerialName("run_id") override val runId: RunId,

    /**
     * The status of the run step, which can be either [Status.InProgress], [Status.Cancelled], [Status.Failed],
     * [Status.Completed], or [Status.Expired].
     */
    @SerialName("status") override val status: Status,

    /**
     * The details of the run step.
     */
    @SerialName("step_details") override val stepDetails: MessageCreationStepDetails,

    /**
     * The last error associated with this run step.
     * Will be null if there are no errors.
     */
    @SerialName("last_error") override val lastError: LastError? = null,

    /**
     * The Unix timestamp (in seconds) for when the run step expired.
     * A step is considered expired if the parent run is expired.
     */
    @SerialName("expired_at") override val expiredAt: Int? = null,

    /**
     * The Unix timestamp (in seconds) for when the run step was [Status.Cancelled].
     */
    @SerialName("cancelled_at") override val cancelledAt: Int? = null,

    /**
     * The Unix timestamp (in seconds) for when the run step failed.
     */
    @SerialName("failed_at") override val failedAt: Int? = null,

    /**
     * The Unix timestamp (in seconds) for when the run step completed.
     */
    @SerialName("completed_at") override val completedAt: Int? = null,

    /**
     * Set of 16 key-value pairs that can be attached to an object.
     * This can be useful for storing additional information about the object in a structured format.
     * Keys can be a maximum of 64 characters long, and values can be a maximum of 512 characters long.
     */
    @SerialName("metadata") override val metadata: Map<String, String>? = null,
): RunStep

@BetaOpenAI
@Serializable
public data class ToolCallsStep(
    /**
     * The identifier of the run step, which can be referenced in API endpoints.
     */
    @SerialName("id") override val id: RunStepId,

    /**
     * The Unix timestamp (in seconds) for when the run step was created.
     */
    @SerialName("created_at") override val createdAt: Int,

    /**
     * The ID of the assistant associated with the run step.
     */
    @SerialName("assistant_id") override val assistantId: AssistantId,

    /**
     * The ID of the thread that was run.
     */
    @SerialName("thread_id") override val threadId: ThreadId,

    /**
     * The ID of the run that this run step is a part of.
     */
    @SerialName("run_id") override val runId: RunId,

    /**
     * The status of the run step, which can be either [Status.InProgress], [Status.Cancelled], [Status.Failed],
     * [Status.Completed], or [Status.Expired].
     */
    @SerialName("status") override val status: Status,

    /**
     * The details of the run step.
     */
    @SerialName("step_details") override val stepDetails: ToolCallStepDetails,

    /**
     * The last error associated with this run step.
     * Will be null if there are no errors.
     */
    @SerialName("last_error") override val lastError: LastError? = null,

    /**
     * The Unix timestamp (in seconds) for when the run step expired.
     * A step is considered expired if the parent run is expired.
     */
    @SerialName("expired_at") override val expiredAt: Int? = null,

    /**
     * The Unix timestamp (in seconds) for when the run step was [Status.Cancelled].
     */
    @SerialName("cancelled_at") override val cancelledAt: Int? = null,

    /**
     * The Unix timestamp (in seconds) for when the run step failed.
     */
    @SerialName("failed_at") override val failedAt: Int? = null,

    /**
     * The Unix timestamp (in seconds) for when the run step completed.
     */
    @SerialName("completed_at") override val completedAt: Int? = null,

    /**
     * Set of 16 key-value pairs that can be attached to an object.
     * This can be useful for storing additional information about the object in a structured format.
     * Keys can be a maximum of 64 characters long, and values can be a maximum of 512 characters long.
     */
    @SerialName("metadata") override val metadata: Map<String, String>? = null,
): RunStep
