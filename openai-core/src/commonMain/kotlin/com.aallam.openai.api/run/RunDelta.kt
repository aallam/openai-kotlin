package com.aallam.openai.api.run

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.core.Event
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a run step delta i.e. any changed fields on a run step during streaming.
 */
@BetaOpenAI
@Serializable
public data class RunDelta(
    /**
     * The identifier of the run step, which can be referenced in API endpoints.
     */
    @SerialName("id") val id: RunId,

    /**
     * The delta containing the fields that have changed on the run step.
     */
    @SerialName("delta") val delta: MessageCreationStepDetails,
): Event
