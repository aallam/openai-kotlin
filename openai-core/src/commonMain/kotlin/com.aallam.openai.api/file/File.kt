package com.aallam.openai.api.file

import com.aallam.openai.api.core.Status
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class File(

    /**
     * A unique id assigned to this file.
     */
    @SerialName("id") val id: FileId,

    /**
     * File size.
     */
    @SerialName("bytes") val bytes: Int,

    /**
     * File creation date.
     */
    @SerialName("created_at") val createdAt: Long,

    /**
     * File name.
     */
    @SerialName("filename") val filename: String,

    /**
     * File purpose.
     */
    @SerialName("purpose") val purpose: Purpose,

    /**
     * File status.
     */
    @SerialName("status") val status: Status,

    /**
     * File format.
     */
    @SerialName("format") val format: String? = null,
)
