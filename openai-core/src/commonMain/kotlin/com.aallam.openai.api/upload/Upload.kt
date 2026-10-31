package com.aallam.openai.api.upload

import com.aallam.openai.api.file.FileId
import com.aallam.openai.api.file.Purpose
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/** Identifier of an intermediate multipart upload. */
@Serializable
@JvmInline
public value class UploadId(public val id: String)

/** Identifier of an uploaded part, ordered when completing its upload. */
@Serializable
@JvmInline
public value class UploadPartId(public val id: String)

/** An intermediate upload. A completed upload exposes the resulting regular file in [file]. */
@Serializable
public data class Upload(
    @SerialName("id") public val id: UploadId,
    @SerialName("bytes") public val bytes: Long,
    @SerialName("created_at") public val createdAt: Long,
    @SerialName("expires_at") public val expiresAt: Long,
    @SerialName("filename") public val filename: String,
    @SerialName("purpose") public val purpose: Purpose,
    @SerialName("status") public val status: UploadStatus,
    @SerialName("file") public val file: UploadedFile? = null,
    @SerialName("object") public val objectType: String = "upload",
)

/** Upload lifecycle state; string-backed to preserve future server states. */
@Serializable
@JvmInline
public value class UploadStatus(public val value: String) {
    public companion object {
        /** The upload accepts more parts. */
        public val Pending: UploadStatus = UploadStatus("pending")
        /** The file was assembled successfully. */
        public val Completed: UploadStatus = UploadStatus("completed")
        /** The upload was cancelled. */
        public val Cancelled: UploadStatus = UploadStatus("cancelled")
        /** The upload expired before completion. */
        public val Expired: UploadStatus = UploadStatus("expired")
    }
}

/** A successfully added part. Its identifier can be passed to [UploadCompletionRequest]. */
@Serializable
public data class UploadPart(
    @SerialName("id") public val id: UploadPartId,
    @SerialName("created_at") public val createdAt: Long,
    @SerialName("upload_id") public val uploadId: UploadId,
    @SerialName("object") public val objectType: String = "upload.part",
)

/**
 * File produced by completing an upload. Unlike older File models, byte counts may be null
 * while processing or larger than a signed 32-bit integer. Its [id] works with regular file APIs.
 */
@Serializable
public data class UploadedFile(
    @SerialName("id") public val id: FileId,
    @SerialName("bytes") public val bytes: Long?,
    @SerialName("created_at") public val createdAt: Long,
    @SerialName("filename") public val filename: String,
    @SerialName("purpose") public val purpose: Purpose,
    @SerialName("status") public val status: String? = null,
    @SerialName("expires_at") public val expiresAt: Long? = null,
    @SerialName("status_details") public val statusDetails: String? = null,
    @SerialName("object") public val objectType: String = "file",
)
