package com.aallam.openai.api.chat

import com.aallam.openai.api.core.Parameters
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.json.*
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
) : ChatTool {

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
    @SerialName("description") public val description: String? = null,
)


/** Tool accepted by Chat Completions, either a function or custom text tool. */
@Serializable(with = ChatToolSerializer::class)
public sealed interface ChatTool

internal object ChatToolSerializer : JsonContentPolymorphicSerializer<ChatTool>(ChatTool::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<ChatTool> =
        when (element.jsonObject["type"]?.jsonPrimitive?.content) {
            "custom" -> CustomTool.serializer()
            "function" -> ChatFunctionTool.serializer()
            else -> throw kotlinx.serialization.SerializationException("Unsupported chat tool type")
        }
}

/** A tool accepting free-form or grammar-constrained text. */
@Serializable
public data class CustomTool(
    /** Custom tool definition. */
    @SerialName("custom") public val custom: CustomToolDefinition,
    /** Tool kind. */
    @EncodeDefault @SerialName("type") public val type: String = "custom",
) : ChatTool

/** Custom tool declaration. */
@Serializable
public data class CustomToolDefinition(
    /** Tool name used in tool calls. */
    @SerialName("name") public val name: String,
    /** Tool purpose. */
    @SerialName("description") public val description: String? = null,
    /** Optional text or grammar input format. */
    @SerialName("format") public val format: CustomToolFormat? = null,
)

/** Custom tool input format: text, or grammar with a definition and syntax. */
@Serializable
public data class CustomToolFormat(
    /** Text or grammar. */
    @SerialName("type") public val type: String,
    /** Grammar required when type is grammar. */
    @SerialName("grammar") public val grammar: CustomToolGrammar? = null,
)

/** Grammar accepted by a custom tool. */
@Serializable
public data class CustomToolGrammar(
    /** Grammar source. */
    @SerialName("definition") public val definition: String,
    /** Grammar syntax: lark or regex. */
    @SerialName("syntax") public val syntax: String,
)

/** Function declaration supporting strict schema enforcement without changing FunctionTool. */
@Serializable
public data class ChatFunctionTool(
    /** Function definition. */
    public val function: ChatFunctionDefinition,
    /** Tool kind. */
    @EncodeDefault public val type: String = "function",
) : ChatTool

/** Rich function definition for advanced Chat requests. */
@Serializable
public data class ChatFunctionDefinition(
    /** Function name. */
    public val name: String,
    /** Function parameter schema. */
    public val parameters: Parameters? = null,
    /** Function purpose. */
    public val description: String? = null,
    /** Enforce the parameter schema. */
    public val strict: Boolean? = null,
)
