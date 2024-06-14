package com.aallam.openai.api.assistant

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.assistant.AssistantTool.*
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
    @SerialName("name") public val name: String? = null,

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
     * Tools can be of types [CodeInterpreter], [FileSearch], or [FunctionTool] for v2.
     * for v1 api tools, use [CodeInterpreter], [RetrievalTool], or [FunctionTool].
     */
    @SerialName("tools") public val tools: List<AssistantTool>,

    /**
     * A list of file IDs attached to this assistant.
     * There can be a maximum of 20 files attached to the assistant.
     * Files are ordered by their creation date in ascending order.
     */
    @Deprecated("For v1 API only")
    @SerialName("file_ids") public val fileIds: List<FileId>,

    /**
     * A list of file IDs attached to this assistant.
     * There can be a maximum of 20 files attached to the assistant.
     * Files are ordered by their creation date in ascending order.
     */
    @SerialName("tool_resources") public val toolResources: ToolResources? = null,

    /**
     * Set of 16 key-value pairs that can be attached to an object.
     * This can be useful for storing additional information about the object in a structured format.
     * Keys can be a maximum of 64 characters long, and values can be a maximum of 512 characters long.
     */
    @SerialName("metadata") public val metadata: Map<String, String>,

    /**
     * What sampling temperature to use, between 0 and 2. Higher values like 0.8 will make the output more random,
     * while lower values like 0.2 will make it more focused and deterministic.
     */
    @SerialName("temperature") public val temperature: Double? = null,

    /**
     * An alternative to sampling with temperature, called nucleus sampling,
     * where the model considers the results of the tokens with top_p probability mass.
     * So 0.1 means only the tokens comprising the top 10% probability mass are considered.
     *
     * We generally recommend altering this or temperature but not both.
     */
    @SerialName("top_p") public val topP: Double? = null,

    /**
     * Specifies the format that the model must output. Compatible with GPT-4o, GPT-4 Turbo, and all GPT-3.5 Turbo
     * models since gpt-3.5-turbo-1106.
     *
     * Setting to [AssistantResponseFormat.JsonObject] enables JSON mode, which guarantees the message the model
     * generates is valid JSON.
     *
     * important: when using JSON mode, you must also instruct the model to produce JSON yourself via a system or user
     * message. Without this, the model may generate an unending stream of whitespace until the generation reaches the
     * token limit, resulting in a long-running and seemingly "stuck" request. Also note that the message content may be
     * partially cut off if finish_reason="length", which indicates the generation exceeded max_tokens or
     * the conversation exceeded the max context length.
     */
    @SerialName("response_format") public val responseFormat: AssistantResponseFormat? = null,
)
