package com.aallam.openai.api.file

import com.aallam.openai.api.core.Status
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * FileResource details.
 */
@Serializable
public data class FileResource(

    /**
     * A unique id assigned to this file.
     */
    @SerialName("id") val id: FileId,

    /**
     * FileResource size.
     */
    @SerialName("bytes") val bytes: Int,

    /**
     * FileResource creation date.
     */
    @SerialName("created_at") val createdAt: Long,

    /**
     * FileResource name.
     */
    @SerialName("filename") val filename: String,

    /**
     * FileResource purpose.
     */
    @SerialName("purpose") val purpose: Purpose,

    /**
     * FileResource status.
     */
    @SerialName("status") val status: Status? = null,

    /**
     * FileResource format.
     */
    @Deprecated("Removed field")
    @SerialName("format") val format: String? = null,

    /**
     * FileResource status details
     */
    @SerialName("status_details") val statusDetails: String? = null,

    /** Unix timestamp when the file expires. */
    @SerialName("expires_at") public val expiresAt: Long? = null,
)
