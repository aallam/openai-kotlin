package com.aallam.openai.api.file

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class FileResponse(

    /**
     * List of files that belong to the user's organization.
     */
    @SerialName("data") val data: List<File>
)
