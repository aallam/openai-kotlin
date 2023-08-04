package com.aallam.openai.api.chat

import com.aallam.openai.api.OpenAIDsl
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class ChatCompletionFunction(
    /**
     * The name of the function to be called. Must be a-z, A-Z, 0-9, or contain underscores and dashes, with a maximum
     * length of 64.
     */
    @SerialName("name") val name: String,
    /**
     * The description of what the function does.
     */
    @SerialName("description") val description: String? = null,
    /**
     * The parameters the functions accepts, described as a JSON Schema object. See the guide for examples and the
     * JSON Schema reference for documentation about the format.
     */
    /**
     * The parameters the functions accepts, described as a JSON Schema object.
     * See the [guide](https://github.com/aallam/openai-kotlin/blob/main/guides/ChatFunctionCall.md) for examples, and the [JSON Schema reference](https://json-schema.org/understanding-json-schema/) for documentation about the format.
     *
     * To describe a function that accepts no parameters, provide [Parameters.Empty]`.
     */
    @SerialName("parameters") val parameters: Parameters,
)

/**
 * Builder of [ChatCompletionFunction] instances.
 */
@OpenAIDsl
public class ChatCompletionFunctionBuilder {

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
    public var parameters: Parameters? = null

    /**
     * Create [ChatCompletionFunction] instance.
     */
    public fun build(): ChatCompletionFunction = ChatCompletionFunction(
        name = requireNotNull(name) { "name is required" },
        description = description,
        parameters = requireNotNull(parameters) { "parameters is required" }
    )
}

/**
 * The function to generate chat completion function instances.
 */
public fun chatCompletionFunction(block: ChatCompletionFunctionBuilder.() -> Unit): ChatCompletionFunction =
    ChatCompletionFunctionBuilder().apply(block).build()
