package com.aallam.openai.api.classification

import com.aallam.openai.api.engine.EngineId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class Classification(

    /**
     * A unique id assigned to this completion.
     */
    @SerialName("completion") public val completion: String,

    /**
     * The category being classified.
     */
    @SerialName("label") public val label: String,

    /**
     * The model that executed the request.
     */
    @SerialName("model") public val model: String,

    /**
     * The engine to use for classification.
     */
    @SerialName("search_model") public val searchModel: EngineId,

    /**
     * List of selected examples.
     */
    @SerialName("selected_examples") public val selectedExamples: List<Example>
)
