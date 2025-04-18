package com.aallam.openai.api.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlin.jvm.JvmInline

/**
 * Controls which (if any) tool is called by the model in the Responses API.
 */
@Serializable(with = ResponseToolChoiceSerializer::class)
public sealed interface ResponseToolChoiceConfig {

    /**
     * Represents a tool choice mode.
     * - `"none"` means the model will not call any tool and instead generates a message.
     * - `"auto"` means the model can pick between generating a message or calling one or more tools.
     * - `"required"` means the model must call one or more tools.
     */
    @JvmInline
    @Serializable
    public value class Mode(public val value: String) : ResponseToolChoiceConfig

    /**
     * Specifies a specific tool the model should use.
     */
    @Serializable
    public data class Named(
        /**
         * The type of tool to use, either "function" or a built-in tool type
         */
        @SerialName("type") public val type: String,

        /**
         * The function details, only used when type is "function"
         */
        @SerialName("function") public val function: FunctionToolChoice? = null,
    ) : ResponseToolChoiceConfig

    public companion object {
        /** Represents the `auto` mode. */
        public val Auto: ResponseToolChoiceConfig = Mode("auto")

        /** Represents the `none` mode. */
        public val None: ResponseToolChoiceConfig = Mode("none")

        /** Represents the `required` mode. */
        public val Required: ResponseToolChoiceConfig = Mode("required")

        /** Specifies a function for the model to call. */
        public fun function(name: String): ResponseToolChoiceConfig =
            Named(type = "function", function = FunctionToolChoice(name = name))

        /** Specifies a file search tool for the model to use. */
        public fun fileSearch(): ResponseToolChoiceConfig = Named(type = "file_search")

        /** Specifies a web search tool for the model to use. */
        public fun webSearch(): ResponseToolChoiceConfig = Named(type = "web_search_preview")

        /** Specifies a web search tool (preview 2025-03-11) for the model to use. */
        public fun webSearch2025(): ResponseToolChoiceConfig = Named(type = "web_search_preview_2025_03_11")

        /** Specifies a computer use tool for the model to use. */
        public fun computerUse(): ResponseToolChoiceConfig = Named(type = "computer_use_preview")
    }
}

/**
 * Represents the function tool choice option.
 */
@Serializable
public data class FunctionToolChoice(
    /**
     * The name of the function to call.
     */
    @SerialName("name") val name: String
)

/**
 * Serializer for [ResponseToolChoiceConfig].
 */
internal class ResponseToolChoiceSerializer :
    JsonContentPolymorphicSerializer<ResponseToolChoiceConfig>(ResponseToolChoiceConfig::class) {
    override fun selectDeserializer(element: JsonElement) = when (element) {
        is JsonPrimitive -> ResponseToolChoiceConfig.Mode.serializer()
        is JsonObject -> ResponseToolChoiceConfig.Named.serializer()
        else -> throw IllegalArgumentException("Unknown element type: $element")
    }
} 