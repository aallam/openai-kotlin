package com.aallam.openai.api.core

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Delete operation response.
 */
@Serializable
public class DeleteResponse(
    @SerialName("id") public val id: String,
    @SerialName("object") public val objectType: String,
    @SerialName("deleted") public val deleted: Boolean
)
