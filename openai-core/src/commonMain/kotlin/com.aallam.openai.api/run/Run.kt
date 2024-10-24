package com.aallam.openai.api.run

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.assistant.AssistantId
import com.aallam.openai.api.assistant.AssistantTool
import com.aallam.openai.api.core.Status
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.api.thread.ThreadId
import com.aallam.openai.api.core.LastError
import com.aallam.openai.api.core.Usage
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents an execution run on a thread.
 */
@Serializable
@BetaOpenAI
public data class Run(
    /**
     * The identifier, which can be referenced in API endpoints.
     */
    @SerialName("id") val id: RunId,

    /**
     * The Unix timestamp (in seconds) for when the run was created.
     */
    @SerialName("created_at") val createdAt: Int,

    /**
     * The ID of the thread that was executed on as a part of this run.
     */
    @SerialName("thread_id") val threadId: ThreadId,

    /**
     * The ID of the assistant used for execution of this run.
     */
    @SerialName("assistant_id") val assistantId: AssistantId,

    /**
     * The status of the run, which can be either [Status.Queued], [Status.InProgress], [Status.RequiresAction],
     * [Status.Cancelling], [Status.Cancelled], [Status.Failed], [Status.Completed], or [Status.Expired].
     */
    @SerialName("status") val status: Status,
    /**
     * Details on the action required to continue the run. Will be `null` if no action is required.
     */
    @SerialName("required_action") val requiredAction: RequiredAction? = null,

    /**
     * The last error associated with this run. Will be null if there are no errors.
     */
    @SerialName("last_error") val lastError: LastError? = null,

    /**
     * The Unix timestamp (in seconds) for when the run will expire.
     */
    @SerialName("expires_at") val expiresAt: Int? = null,

    /**
     * The Unix timestamp (in seconds) for when the run was started.
     */
    @SerialName("started_at") val startedAt: Int? = null,

    /**
     * The Unix timestamp (in seconds) for when the run was cancelled.
     */
    @SerialName("cancelled_at") val cancelledAt: Int? = null,

    /**
     * The Unix timestamp (in seconds) for when the run failed.
     */
    @SerialName("failed_at") val failedAt: Int? = null,

    /**
     * The Unix timestamp (in seconds) for when the run was completed.
     */
    @SerialName("completed_at") val completedAt: Int? = null,

    /**
     * The model that the assistant used for this run.
     */
    @SerialName("model") val model: ModelId,

    /**
     * The instructions that the assistant used for this run.
     */
    @SerialName("instructions") val instructions: String? = null,

    /**
     * The list of tools that the assistant used for this run.
     */
    @SerialName("tools") val tools: List<AssistantTool>? = null,

    /**
     * The list of File IDs the assistant used for this run.
     */
    @SerialName("file_ids") val fileIds: List<String>? = null,

    /**
     * Set of 16 key-value pairs that can be attached to an object.
     * This can be useful for storing additional information about the object in a structured format.
     * Keys can be a maximum of 64 characters long, and values can be a maximum of 512 characters long.
     */
    @SerialName("metadata") val metadata: Map<String, String>? = null,

    /**
     * Usage statistics related to the run.
     * This value will be null if the run is not in a terminal state (i.e. in_progress, queued, etc.).
     */
    @SerialName("usage") public val usage: Usage? = null,

    /**
     * The sampling temperature used for this run. If not set, defaults to 1.
     */
    @SerialName("temperature") val temperature: Double? = null,

    /**
     * The nucleus sampling value used for this run. If not set, defaults to 1.
     */
    @SerialName("top_p") val topP: Double? = null,

    /**
     * The maximum number of prompt tokens specified to have been used over the course of the run.
     */
    @SerialName("max_prompt_tokens") val maxPromptTokens: Int? = null,

    /**
     * The maximum number of completion tokens specified to have been used over the course of the run.
     */
    @SerialName("max_completion_tokens") val maxCompletionTokens: Int? = null,
)
