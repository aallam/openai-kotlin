package com.aallam.openai.api.chat

import com.aallam.openai.api.core.Parameters
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
         * @param parameters The parameters the function accepts, described as a JSON Schema object.
         */
        public fun function(name: String, description: String? = null, parameters: Parameters): Tool =
            Tool(
                type = ToolType.Function,
                function = FunctionTool(name = name, description = description, parameters = parameters)
            )
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
     * The parameters the functions accept, described as a JSON Schema object.
     * See the [guide](https://platform.openai.com/docs/guides/text-generation/function-calling) for examples,
     * and the [JSON Schema reference](https://json-schema.org/understanding-json-schema) for documentation about
     * the format.
     *
     * Omitting `parameters` defines a function with an empty parameter list.
     */
    @SerialName("parameters") val parameters: Parameters? = null,

    /**
     * A description of what the function does, used by the model to choose when and how to call the function.
     */
    @SerialName("description") public val description: String? = null
)
