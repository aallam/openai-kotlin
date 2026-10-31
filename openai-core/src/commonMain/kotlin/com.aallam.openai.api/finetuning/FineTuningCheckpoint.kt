package com.aallam.openai.api.finetuning

import com.aallam.openai.api.model.ModelId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** A fine-tuning resource page with its full cursor envelope. */
@Serializable
public data class FineTuningPage<T>(
    /** Resources in the current page. */
    public val data: List<T>,
    /** Whether another page exists. */
    @SerialName("has_more") public val hasMore: Boolean,
    /** Resource object type. */
    @SerialName("object") public val objectType: String,
    /** First resource identifier, when supplied. */
    @SerialName("first_id") public val firstId: String? = null,
    /** Cursor for the next page, when supplied. */
    @SerialName("last_id") public val lastId: String? = null,
) : List<T> by data

/** A checkpoint produced at a particular training step. */
@Serializable
public data class FineTuningCheckpoint(
    /** Checkpoint resource identifier. */
    public val id: String,
    /** Creation timestamp in Unix seconds. */
    @SerialName("created_at") public val createdAt: Long,
    /** Model identifier used by checkpoint-permission operations. */
    @SerialName("fine_tuned_model_checkpoint") public val fineTunedModelCheckpoint: ModelId,
    /** Training job that produced the checkpoint. */
    @SerialName("fine_tuning_job_id") public val fineTuningJobId: FineTuningId,
    /** Available measurements at this checkpoint. */
    public val metrics: FineTuningCheckpointMetrics,
    /** Resource object type. */
    @SerialName("object") public val objectType: String,
    /** Training step that produced the checkpoint. */
    @SerialName("step_number") public val stepNumber: Long,
)

/** Optional training and validation measurements for a checkpoint. */
@Serializable
public data class FineTuningCheckpointMetrics(
    /** Loss across the complete validation set. */
    @SerialName("full_valid_loss") public val fullValidLoss: Double? = null,
    /** Token accuracy across the complete validation set. */
    @SerialName("full_valid_mean_token_accuracy") public val fullValidMeanTokenAccuracy: Double? = null,
    /** Measurement step. */
    public val step: Double? = null,
    /** Training loss. */
    @SerialName("train_loss") public val trainLoss: Double? = null,
    /** Training token accuracy. */
    @SerialName("train_mean_token_accuracy") public val trainMeanTokenAccuracy: Double? = null,
    /** Validation loss. */
    @SerialName("valid_loss") public val validLoss: Double? = null,
    /** Validation token accuracy. */
    @SerialName("valid_mean_token_accuracy") public val validMeanTokenAccuracy: Double? = null,
)

/** Access granted to a project for a fine-tuned model checkpoint. */
@Serializable
public data class FineTuningCheckpointPermission(
    /** Permission identifier. */
    public val id: String,
    /** Creation timestamp in Unix seconds. */
    @SerialName("created_at") public val createdAt: Long,
    /** Resource object type. */
    @SerialName("object") public val objectType: String,
    /** Project receiving access. */
    @SerialName("project_id") public val projectId: String,
)

/** Projects to grant access to a checkpoint. Requires an Admin API key. */
@Serializable
public data class FineTuningCheckpointPermissionRequest(
    /** Project identifiers that should receive access. */
    @SerialName("project_ids") public val projectIds: List<String>,
)

/** Result of removing a checkpoint permission. */
@Serializable
public data class FineTuningPermissionDeletion(
    /** Removed permission identifier. */
    public val id: String,
    /** Whether the permission was removed. */
    public val deleted: Boolean,
    /** Resource object type. */
    @SerialName("object") public val objectType: String,
)

/** Checkpoint-permission ordering uses full words rather than the ordinary asc/desc query values. */
@Serializable
public enum class FineTuningPermissionOrder {
    /** Oldest permissions first. */
    @SerialName("ascending") Ascending,
    /** Newest permissions first. */
    @SerialName("descending") Descending,
}
