package com.aallam.openai.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class Model(
    @SerialName("id") public val id: String,
    @SerialName("created") public val created: Long,
    @SerialName("owned_by") public val ownedBy: String,
    @SerialName("permission") public val permission: List<ModelPermission>,
)
