package com.aallam.openai.api.chat

import com.aallam.openai.api.chat.internal.ToolType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A list of tools the model may call. Use this to provide a list of functions the model may generate JSON inputs for.
 */
@Serializable
public data class Tool(

    /**
     * The type of the tool.
     */
    @SerialName("type") val type: ToolType,

    /**
     * A description of what the function does, used by the model to choose when and how to call the function.
     */
    @SerialName("function") val function: FunctionTool,
) {

    public companion object {

        /**
         * Creates a 'function' tool.
         *
         * @param name The name of the function to be called. Must be a-z, A-Z, 0-9, or contain underscores and dashes,
         * with a maximum length of 64.
         * @param parameters The parameters the functions accepts, described as a JSON Schema object.
         */
        public fun function(name: String, parameters: Parameters): Tool =
            Tool(type = ToolType.Function, function = FunctionTool(name = name, parameters = parameters))
    }
}

/**
 * A description of what the function does, used by the model to choose when and how to call the function.
 */
@Serializable
public data class FunctionTool(
    /**
     * The name of the function to be called. Must be a-z, A-Z, 0-9, or contain underscores and dashes, with a maximum
     * length of 64.
     */
    @SerialName("name") val name: String,

    /**
     * The parameters the functions accepts, described as a JSON Schema object.
     * See the [guide](https://github.com/aallam/openai-kotlin/blob/main/guides/ChatFunctionCall.md) for examples,
     * and the [JSON Schema reference](https://json-schema.org/understanding-json-schema/) for documentation about
     * the format.
     *
     * To describe a function that accepts no parameters, provide [Parameters.Empty]`.
     */
    @SerialName("parameters") val parameters: Parameters
)
