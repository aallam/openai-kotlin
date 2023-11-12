package com.aallam.openai.api.assistant

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.OpenAIDsl
import com.aallam.openai.api.file.FileId
import com.aallam.openai.api.model.ModelId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@BetaOpenAI
@Serializable
public data class AssistantRequest(
    /**
     * ID of the model to use.
     * This is required if the assistant does not yet exist.
     */
    @SerialName("model") val model: ModelId? = null,

    /**
     * The name of the assistant. Optional. The maximum length is 256 characters.
     */
    @SerialName("name") val name: String? = null,

    /**
     * The description of the assistant. Optional. The maximum length is 512 characters.
     */
    @SerialName("description") val description: String? = null,

    /**
     * The system instructions that the assistant uses. Optional. The maximum length is 32768 characters.
     */
    @SerialName("instructions") val instructions: String? = null,

    /**
     * A list of tools enabled on the assistant. Optional. Defaults to an empty list.
     * Tools can be of types code_interpreter, retrieval, or function.
     */
    @SerialName("tools") val tools: List<AssistantTool>? = null,

    /**
     * A list of file IDs attached to this assistant. Optional. Defaults to an empty list.
     * There can be a maximum of 20 files attached to the assistant.
     */
    @SerialName("file_ids") val fileIds: List<FileId>? = null,

    /**
     * Set of 16 key-value pairs that can be attached to an object. Optional.
     * Keys can be a maximum of 64 characters long, and values can be a maximum of 512 characters long.
     */
    @SerialName("metadata") val metadata: Map<String, String>? = null
)

@BetaOpenAI
@OpenAIDsl
public class AssistantRequestBuilder {

    /**
     * The name of the assistant. The maximum length is 256 characters.
     */
    public var name: String? = null

    /**
     * The description of the assistant. The maximum length is 512 characters.
     */
    public var description: String? = null

    /**
     * ID of the model to use.
     */
    public var model: ModelId? = null

    /**
     * The system instructions that the assistant uses. The maximum length is 32768 characters.
     */
    public var instructions: String? = null

    /**
     * A list of tools enabled on the assistant.
     */
    public var tools: List<AssistantTool>? = null

    /**
     * A list of file IDs attached to this assistant.
     */
    public var fileIds: List<FileId>? = null

    /**
     * Set of 16 key-value pairs that can be attached to an object.
     */
    public var metadata: Map<String, String>? = null

    /**
     * Create [Assistant] instance.
     */
    public fun build(): AssistantRequest = AssistantRequest(
        model = model,
        name = name,
        description = description,
        instructions = instructions,
        tools = tools,
        fileIds = fileIds,
        metadata = metadata,
    )
}

/**
 * Creates [AssistantRequest] instance.
 */
@BetaOpenAI
public fun assistantRequest(block: AssistantRequestBuilder.() -> Unit): AssistantRequest =
    AssistantRequestBuilder().apply(block).build()
