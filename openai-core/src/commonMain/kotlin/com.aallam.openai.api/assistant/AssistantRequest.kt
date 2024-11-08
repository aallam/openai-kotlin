package com.aallam.openai.api.assistant

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.OpenAIDsl
import com.aallam.openai.api.core.ResponseFormat
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
     * A set of resources that are used by the assistant's tools. The resources are specific to the type of tool.
     * For example, the code_interpreter tool requires a list of file IDs, while the file_search tool requires a list of vector store IDs.
     */
    @SerialName("tool_resources") val toolResources: ToolResources? = null,

    /**
     * Set of 16 key-value pairs that can be attached to an object. Optional.
     * Keys can be a maximum of 64 characters long, and values can be a maximum of 512 characters long.
     */
    @SerialName("metadata") val metadata: Map<String, String>? = null,

    /**
     * What sampling temperature to use, between 0 and 2. Higher values like 0.8 will make the output more random,
     * while lower values like 0.2 will make it more focused and deterministic.
     */
    @SerialName("temperature") val temperature: Double? = null,

    /**
     * An alternative to sampling with temperature, called nucleus sampling,
     * where the model considers the results of the tokens with top_p probability mass.
     * So 0.1 means only the tokens comprising the top 10% probability mass are considered.
     *
     * We generally recommend altering this or temperature but not both.
     */
    @SerialName("top_p") val topP: Double? = null,

    /**
     * Specifies the format that the model must output. Compatible with GPT-4o, GPT-4 Turbo, and all GPT-3.5 Turbo
     * models since gpt-3.5-turbo-1106.
     *
     * Setting to [ResponseFormat.JsonSchemaResponseFormat] enables Structured Outputs which ensures the model will match your supplied JSON schema.
     *
     * Structured Outputs [ResponseFormat.JsonSchemaResponseFormat] are available in our latest large language models, starting with GPT-4o:
     * 1. gpt-4o-mini-2024-07-18 and later
     * 2. gpt-4o-2024-08-06 and later
     *
     * Older models like gpt-4-turbo and earlier may use JSON mode [ResponseFormat.JsonObjectResponseFormat] instead.
     *
     * Setting to [ResponseFormat.JsonObjectResponseFormat] enables JSON mode, which guarantees the message the model
     * generates is valid JSON.
     *
     * important: when using JSON mode, you must also instruct the model to produce JSON yourself via a system or user
     * message. Without this, the model may generate an unending stream of whitespace until the generation reaches the
     * token limit, resulting in a long-running and seemingly "stuck" request. Also note that the message content may be
     * partially cut off if finish_reason="length", which indicates the generation exceeded max_tokens or
     * the conversation exceeded the max context length.
     */
    @SerialName("response_format") val responseFormat: ResponseFormat? = null,
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
     * A set of resources that are used by the assistant's tools. The resources are specific to the type of tool.
     */
    public var toolResources: ToolResources? = null

    /**
     * Set of 16 key-value pairs that can be attached to an object.
     */
    public var metadata: Map<String, String>? = null

    /**
     * What sampling temperature to use, between 0 and 2.
     */
    public var temperature: Double? = null

    /**
     * An alternative to sampling with temperature, called nucleus sampling,
     * where the model considers the results of the tokens with top_p probability mass.
     */
    public var topP: Double? = null

    /**
     * Specifies the format that the model must output. Compatible with GPT-4o, GPT-4 Turbo, and all GPT-3.5 Turbo
     * models since gpt-3.5-turbo-1106.
     *
     * Setting to [OldResponseFormat.JSON_SCHEMA] enables Structured Outputs which ensures the model will match your supplied JSON schema.
     *
     * Structured Outputs ([OldResponseFormat.JSON_SCHEMA]) are available in our latest large language models, starting with GPT-4o:
     * 1. gpt-4o-mini-2024-07-18 and later
     * 2. gpt-4o-2024-08-06 and later
     *
     * Older models like gpt-4-turbo and earlier may use JSON mode ([OldResponseFormat.JSON_OBJECT]) instead.
     *
     * Setting to [OldResponseFormat.JSON_OBJECT] enables JSON mode, which guarantees the message the model
     * generates is valid JSON.
     *
     * important: when using JSON mode, you must also instruct the model to produce JSON yourself via a system or user
     * message. Without this, the model may generate an unending stream of whitespace until the generation reaches the
     * token limit, resulting in a long-running and seemingly "stuck" request. Also note that the message content may be
     * partially cut off if finish_reason="length", which indicates the generation exceeded max_tokens or
     * the conversation exceeded the max context length.
     */
    public var responseFormat: ResponseFormat? = null

    /**
     * Create [Assistant] instance.
     */
    public fun build(): AssistantRequest = AssistantRequest(
        model = model,
        name = name,
        description = description,
        instructions = instructions,
        tools = tools,
        metadata = metadata,
        temperature = temperature,
        topP = topP,
        responseFormat = responseFormat,
    )
}

/**
 * Creates [AssistantRequest] instance.
 */
@BetaOpenAI
public fun assistantRequest(block: AssistantRequestBuilder.() -> Unit): AssistantRequest =
    AssistantRequestBuilder().apply(block).build()
