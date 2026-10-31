package com.aallam.openai.api.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** URL, file or container citation in Responses output. */
@Serializable
public data class ResponseAnnotation(
    @SerialName("type") val type: String,
    @SerialName("url") val url: String? = null,
    @SerialName("title") val title: String? = null,
    @SerialName("start_index") val startIndex: Int? = null,
    @SerialName("end_index") val endIndex: Int? = null,
    @SerialName("text") val text: String? = null,
    /** Cited file identifier. */
    @SerialName("file_id") public val fileId: String? = null,
    /** Cited file name. */
    @SerialName("filename") public val filename: String? = null,
    /** Citation index. */
    @SerialName("index") public val index: Int? = null,
    /** Container holding the cited file. */
    @SerialName("container_id") public val containerId: String? = null,
)
