package com.aallam.openai.api.assistant

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.OpenAIDsl
import com.aallam.openai.api.core.Parameters
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@BetaOpenAI
@Serializable
public data class Function(
    /**
     * The name of the function to be called. Must be a-z, A-Z, 0-9, or contain underscores and dashes, with a maximum
     * length of 64.
     */
    @SerialName("name") val name: String,
    /**
     * The description of what the function does. used by the model to choose when and how to call the function.
     */
    @SerialName("description") val description: String,
    /**
     * The parameters the functions accept, described as a JSON Schema object.
     * See [JSON Schema reference](https://json-schema.org/understanding-json-schema/) for documentation about the format.
     *
     * To describe a function that accepts no parameters, provide [Parameters.Empty]`.
     */
    @SerialName("parameters") val parameters: Parameters,
    /**
     * Whether to enable strict schema adherence when generating the function call.
     * If set to true, the model will always follow the exact schema defined in the parameters field.
     * Only a subset of JSON Schema is supported when strict is true.
     * To learn more about Structured Outputs in the [function calling guide](https://platform.openai.com/docs/api-reference/assistants/docs/guides/function-calling).
     */
    val strict: Boolean? = null
)

/**
 * Builder of [Function] instances.
 */
@BetaOpenAI
@OpenAIDsl
public class FunctionBuilder {

    /**
     * The name of the function to be called.
     */
    public var name: String? = null

    /**
     * The description of what the function does.
     */
    public var description: String? = null

    /**
     * The parameters the function accepts.
     */
    public var parameters: Parameters? = Parameters.Empty

    /**
     * Whether to enable strict schema adherence when generating the function call.
     * If set to true, the model will always follow the exact schema defined in the parameters field.
     * Only a subset of JSON Schema is supported when strict is true.
     * To learn more about Structured Outputs in the [function calling guide](https://platform.openai.com/docs/api-reference/assistants/docs/guides/function-calling).
     */
    public var strict: Boolean? = null

    /**
     * Create [Function] instance.
     */
    public fun build(): Function = Function(
        name = requireNotNull(name) { "name is required" },
        description = requireNotNull(description) { "description is required" },
        parameters = requireNotNull(parameters) { "parameters is required" },
        strict = strict
    )
}

/**
 * Creates [Function] instance.
 */
@BetaOpenAI
public fun function(block: FunctionBuilder.() -> Unit): Function =
    FunctionBuilder().apply(block).build()
