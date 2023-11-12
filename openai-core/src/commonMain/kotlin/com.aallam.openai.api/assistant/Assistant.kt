package com.aallam.openai.api.assistant

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.file.FileId
import com.aallam.openai.api.model.ModelId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@BetaOpenAI
@Serializable
public data class Assistant(
    /**
     * The identifier, which can be referenced in API endpoints.
     */
    @SerialName("id") public val id: AssistantId,
    /**
     * The Unix timestamp (in seconds) for when the assistant was created.
     */
    @SerialName("created_at") public val createdAt: Long,

    /**
     * The name of the assistant. The maximum length is 256 characters.
     */
    @SerialName("name") public val name: String,

    /**
     * The description of the assistant. The maximum length is 512 characters.
     */
    @SerialName("description") public val description: String? = null,

    /**
     * ID of the model to use. You can use the [List](https://platform.openai.com/docs/api-reference/models/list) models
     * API to see all of your [available models](https://platform.openai.com/docs/models/overview), or see our Model
     * overview for descriptions of them.
     */
    @SerialName("model") public val model: ModelId,

    /**
     * The system instructions that the assistant uses. The maximum length is 32768 characters.
     */
    @SerialName("instructions") public val instructions: String? = null,

    /**
     * A list of tool enabled on the assistant.
     * There can be a maximum of 128 tools per assistant.
     * Tools can be of types [CodeInterpreterTool], [RetrievalTool], or [FunctionTool].
     */
    @SerialName("tools") public val tools: List<AssistantTool>,

    /**
     * A list of file IDs attached to this assistant.
     * There can be a maximum of 20 files attached to the assistant.
     * Files are ordered by their creation date in ascending order.
     */
    @SerialName("file_ids") public val fileIds: List<FileId>,

    /**
     * Set of 16 key-value pairs that can be attached to an object.
     * This can be useful for storing additional information about the object in a structured format.
     * Keys can be a maximum of 64 characters long, and values can be a maximum of 512 characters long.
     */
    @SerialName("metadata") public val metadata: Map<String, String>,
)
