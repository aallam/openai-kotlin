package com.aallam.openai.api.run

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.assistant.AssistantId
import com.aallam.openai.api.assistant.AssistantResponseFormat
import com.aallam.openai.api.assistant.AssistantTool
import com.aallam.openai.api.assistant.AssistantTruncationStrategy
import com.aallam.openai.api.model.ModelId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Create a run request.
 */
@BetaOpenAI
@Serializable
public data class RunRequest(
    /**
     * The ID of the assistant to use to execute this run.
     */
    @SerialName("assistant_id") val assistantId: AssistantId,

    /**
     * The ID of the Model to be used to execute this run.
     * If a value is provided here, it will override the model associated with the assistant.
     * If not, the model associated with the assistant will be used.
     */
    @SerialName("model") val model: ModelId? = null,

    /**
     * Override the default system message of the assistant.
     * This is useful for modifying the behavior on a per-run basis.
     */
    @SerialName("instructions") val instructions: String? = null,

    /**
     * Appends additional instructions at the end of the instructions for the run.
     * This is useful for modifying the behavior on a per-run basis without overriding other instructions.
     */
    @SerialName("additional_instructions") val additionalInstructions: String? = null,

    /**
     * Override the tools the assistant can use for this run.
     * This is useful for modifying the behavior on a per-run basis.
     */
    @SerialName("tools") val tools: List<AssistantTool>? = null,

    /**
     * Set of 16 key-value pairs that can be attached to an object.
     * This can be useful for storing additional information about the object in a structured format.
     * Keys can be a maximum of 64 characters long, and values can be a maximum of 512 characters long.
     */
    @SerialName("metadata") val metadata: Map<String, String>? = null,

    /**
     * What sampling temperature to use, between 0 and 2.
     * Higher values like 0.8 will make the output more random, while lower values like 0.2 will make it more focused and deterministic.
     */
    @SerialName("temperature") val temperature: Int? = null,

    /**
     * An alternative to sampling with temperature, called nucleus sampling, where the model considers the results of the tokens with top_p probability mass.
     * So 0.1 means only the tokens comprising the top 10% probability mass are considered.
     */
    @SerialName("top_p") val topP: Int? = null,

    /**
     * The maximum number of prompt tokens that may be used over the course of the run.
     * The run will make a best effort to use only the number of prompt tokens specified, across multiple turns of the run.
     * If the run exceeds the number of prompt tokens specified, the run will end with status `incomplete`.
     * See `incomplete_details` for more info.
     */
    @SerialName("max_prompt_tokens") val maxPromptTokens: Int? = null,

    /**
     * The maximum number of completion tokens that may be used over the course of the run.
     * The run will make a best effort to use only the number of completion tokens specified, across multiple turns of the run.
     * If the run exceeds the number of completion tokens specified, the run will end with status `incomplete`.
     * See `incomplete_details` for more info.
     */
    @SerialName("max_completion_tokens") val maxCompletionTokens: Int? = null,

    /**
     * Controls for how a thread will be truncated prior to the run. Use this to control the intial context window of the run.
     */
    @SerialName("truncation_strategy") val truncationStrategy: AssistantTruncationStrategy? = null,

    /**
     * Specifies the format that the model must output.
     * Compatible with GPT-4o, GPT-4 Turbo, and all GPT-3.5 Turbo models since gpt-3.5-turbo-1106.
     */
    @SerialName("response_format") val responseFormat: AssistantResponseFormat? = null,
)

/**
 * Create a run request.
 */
@BetaOpenAI
public fun runRequest(block: RunRequestBuilder.() -> Unit): RunRequest = RunRequestBuilder().apply(block).build()

/**
 * Builder for [RunRequest].
 */
@BetaOpenAI
public class RunRequestBuilder {

    /**
     * The ID of the assistant to use to execute this run.
     */
    public var assistantId: AssistantId? = null

    /**
     * The ID of the Model to be used to execute this run.
     * If a value is provided here, it will override the model associated with the assistant.
     * If not, the model associated with the assistant will be used.
     */
    public var model: ModelId? = null

    /**
     * Override the default system message of the assistant.
     * This is useful for modifying the behavior on a per-run basis.
     */
    public var instructions: String? = null

    /**
     * Appends additional instructions at the end of the instructions for the run.
     * This is useful for modifying the behavior on a per-run basis without overriding other instructions.
     */
    public var additionalInstructions: String? = null

    /**
     * Override the tools the assistant can use for this run.
     * This is useful for modifying the behavior on a per-run basis.
     */
    public var tools: List<AssistantTool>? = null

    /**
     * Set of 16 key-value pairs that can be attached to an object.
     * This can be useful for storing additional information about the object in a structured format.
     * Keys can be a maximum of 64 characters long, and values can be a maximum of 512 characters long.
     */
    public var metadata: Map<String, String>? = null

    /**
     * Build a [RunRequest] instance.
     */
    public fun build(): RunRequest = RunRequest(
        assistantId = requireNotNull(assistantId) { "assistantId is required" },
        model = model,
        instructions = instructions,
        additionalInstructions = additionalInstructions,
        tools = tools,
        metadata = metadata,
    )
}
