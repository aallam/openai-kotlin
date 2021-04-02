package com.aallam.openai.api.file

import kotlinx.serialization.Serializable

@Serializable
public data class FileResponse(

    /**
     * List of files that belong to the user's organization.
     */
    val data: List<File>
)
