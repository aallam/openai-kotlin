package com.aallam.openai.client

import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.finetuning.*
import com.aallam.openai.api.model.ModelId

/** Shares checkpoint models between projects. All operations require an Admin API key. */
public interface FineTuningPermissions {
    /** Grants checkpoint-model access to the supplied projects and returns created permissions. */
    public suspend fun createFineTuningCheckpointPermissions(
        checkpoint: ModelId,
        request: FineTuningCheckpointPermissionRequest,
        requestOptions: RequestOptions? = null,
    ): FineTuningPage<FineTuningCheckpointPermission> =
        throw UnsupportedOperationException("Fine-tuning permissions are not supported by this client.")

    /** Lists checkpoint-model permissions with optional project filtering and cursor pagination. */
    public suspend fun fineTuningCheckpointPermissions(
        checkpoint: ModelId,
        after: String? = null,
        limit: Int? = null,
        order: FineTuningPermissionOrder? = null,
        projectId: String? = null,
        requestOptions: RequestOptions? = null,
    ): FineTuningPage<FineTuningCheckpointPermission> =
        throw UnsupportedOperationException("Fine-tuning permissions are not supported by this client.")

    /** Removes one checkpoint-model permission and retains the deletion result. */
    public suspend fun deleteFineTuningCheckpointPermission(
        checkpoint: ModelId,
        permissionId: String,
        requestOptions: RequestOptions? = null,
    ): FineTuningPermissionDeletion =
        throw UnsupportedOperationException("Fine-tuning permissions are not supported by this client.")
}
