package com.aallam.openai.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Response to the models list query.
 */
@Serializable
public class ModelsResponse(
    /**
     * List containing the actual results.
     */
    @SerialName("data") public val data: List<Model>,
)
