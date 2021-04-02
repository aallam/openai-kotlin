package com.aallam.openai.api.answer

import com.aallam.openai.api.engine.EngineId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class Answer(

    /**
     * List of answers.
     */
    @SerialName("answers") val answers: List<String>,

    /**
     * A unique id assigned to this completion.
     */
    @SerialName("completion") val completion: String,

    /**
     * The model that executed the request.
     */
    @SerialName("model") val model: String,

    /**
     * The engine to use for classification.
     */
    @SerialName("search_model") val searchModel: EngineId,

    /**
     * List of selected documents.
     */
    @SerialName("selected_documents") val selectedDocuments: List<Document>
)
