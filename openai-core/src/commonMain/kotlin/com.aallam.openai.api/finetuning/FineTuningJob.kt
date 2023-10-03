package com.aallam.openai.api.finetuning;

import com.aallam.openai.api.core.OrganizationId
import com.aallam.openai.api.core.Status
import com.aallam.openai.api.file.FileId
import com.aallam.openai.api.model.ModelId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A data class representing a fine-tuning job.
 */
@Serializable
public data class FineTuningJob(

    /**
     * The object identifier, which can be referenced in the API endpoints.
     */
    @SerialName("id")
    val id: FineTuningId,

    /**
     * The Unix timestamp (in seconds) for when the fine-tuning job was created.
     */
    @SerialName("created_at")
    val createdAt: Int,

    /**
     * The base model that is being fine-tuned.
     */
    @SerialName("model")
    val model: ModelId,

    /**
     * The organization that owns the fine-tuning job.
     */
    @SerialName("organization_id")
    val organizationId: OrganizationId,

    /**
     * The current status of the fine-tuning job (e.g., [Status.ValidatingFiles], [Status.Queued], etc.).
     */
    @SerialName("status")
    val status: Status,

    /**
     * The hyperparameters used for the fine-tuning job.
     */
    @SerialName("hyperparameters")
    val hyperparameters: Hyperparameters,

    /**
     * The file ID used for training, retrievable via the Files API.
     */
    @SerialName("training_file")
    val trainingFile: FileId,

    /**
     * The compiled results file ID(s) for the fine-tuning job, retrievable via the Files API.
     */
    @SerialName("result_files")
    val resultFiles: List<FileId>,

    /**
     * The Unix timestamp (in seconds) for when the fine-tuning job was finished, or null if still running.
     */
    @SerialName("finished_at")
    val finishedAt: Int? = null,

    /**
     * The name of the fine-tuned model that is being created, or null if the fine-tuning job is still running.
     */
    @SerialName("fine_tuned_model")
    val fineTunedModel: ModelId? = null,

    /**
     * The file ID used for validation, retrievable via the Files API, or null if not available.
     */
    @SerialName("validation_file")
    val validationFile: FileId? = null,

    /**
     * The total number of billable tokens processed by this fine-tuning job, or null if the job is still running.
     */
    @SerialName("trained_tokens")
    val trainedTokens: Int? = null,

    /**
     * Contains more information on the cause of failure for failed fine-tuning jobs, or null if not failed.
     */
    @SerialName("error")
    val error: ErrorInfo? = null,
)
