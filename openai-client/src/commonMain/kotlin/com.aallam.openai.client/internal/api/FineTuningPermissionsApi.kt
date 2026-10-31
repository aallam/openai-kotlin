package com.aallam.openai.client.internal.api

import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.finetuning.*
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.FineTuningPermissions
import com.aallam.openai.client.internal.extension.requestOptions
import com.aallam.openai.client.internal.http.HttpRequester
import com.aallam.openai.client.internal.http.perform
import io.ktor.client.request.*
import io.ktor.http.*

internal class FineTuningPermissionsApi(private val requester: HttpRequester) : FineTuningPermissions {
    override suspend fun createFineTuningCheckpointPermissions(
        checkpoint: ModelId,
        request: FineTuningCheckpointPermissionRequest,
        requestOptions: RequestOptions?,
    ): FineTuningPage<FineTuningCheckpointPermission> = requester.perform {
        it.post {
            url(path = permissionPath(checkpoint))
            contentType(ContentType.Application.Json)
            setBody(request)
            requestOptions(requestOptions)
        }
    }

    override suspend fun fineTuningCheckpointPermissions(
        checkpoint: ModelId,
        after: String?,
        limit: Int?,
        order: FineTuningPermissionOrder?,
        projectId: String?,
        requestOptions: RequestOptions?,
    ): FineTuningPage<FineTuningCheckpointPermission> = requester.perform {
        it.get {
            url(path = permissionPath(checkpoint)) {
                after?.let { parameter("after", it) }
                limit?.let { parameter("limit", it) }
                order?.let { parameter("order", it.name.lowercase()) }
                projectId?.let { parameter("project_id", it) }
            }
            requestOptions(requestOptions)
        }
    }

    override suspend fun deleteFineTuningCheckpointPermission(
        checkpoint: ModelId,
        permissionId: String,
        requestOptions: RequestOptions?,
    ): FineTuningPermissionDeletion = requester.perform {
        it.delete {
            url(path = "${permissionPath(checkpoint)}/${segment(permissionId)}")
            requestOptions(requestOptions)
        }
    }

    private fun permissionPath(checkpoint: ModelId): String = "fine_tuning/checkpoints/${segment(checkpoint.id)}/permissions"

    private fun segment(id: String): String {
        require(id.isNotEmpty() && id != "." && id != ".." && id.none { it in "/?#%\\" || it.isWhitespace() || it.code < 32 }) {
            "Checkpoint and permission identifiers must be single path segments"
        }
        return id
    }
}
