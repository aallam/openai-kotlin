package com.aallam.openai.api.finetune

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Fine tune event.
 */
@Serializable
public data class FineTuneEvent(

    /**
     * Creation date.
     */
    @SerialName("created_at") val createdAt: Long,

    /**
     * Fine tune event level.
     */
    @SerialName("level") val level: String,

    /**
     * Fine tune event message.
     */
    @SerialName("message") val message: String
)
