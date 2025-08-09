package com.aallam.openai.api.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlin.jvm.JvmInline

/**
 * How the model should select which tool (or tools) to use when generating a response.
 * See the tools parameter to see how to specify which tools the model can call.
 */
@Serializable(with = ResponseToolChoiceSerializer::class)
public sealed interface ResponseToolChoiceConfig {

    /**
     * Represents a tool choice mode.
     * - `none` means the model will not call any tool and instead generates a message.
     * - `auto` means the model can pick between generating a message or calling one or more tools.
     * - `required` means the model must call one or more tools.
     */
    @JvmInline
    @Serializable
    public value class Mode(public val value: String) : ResponseToolChoiceConfig {
        public companion object {
            public val Auto: Mode = Mode("auto")
            public val None: Mode = Mode("none")
            public val Required: Mode = Mode("required")
        }
    }

    @Serializable
    @SerialName("file_search")
    public class FileSearch : ResponseToolChoice

    @Serializable
    @SerialName("web_search_preview")
    public class WebSearchPreview : ResponseToolChoice

    @Serializable
    @SerialName("computer_use_preview")
    public class ComputerUsePreview : ResponseToolChoice

    @Serializable
    @SerialName("code_interpreter")
    public class CodeInterpreter : ResponseToolChoice

    //TODO add image_generation after updates to image API are implemented

    /**
     * Use this option to force the model to call a specific function.
     */
    @Serializable
    @SerialName("function")
    public data class Function(
        /** The name of the function to call. */
        @SerialName("name") val name: String
    ) : ResponseToolChoice

    /**
     * Use this option to force the model to call a specific tool on a remote MCP server.
     */
    @Serializable
    @SerialName("mcp")
    public data class MCPTool(
        /** The label of the MCP server to use. */
        @SerialName("server_label") val serverLabel: String,

        /** The name of the tool to call on the server.*/
        @SerialName("name") val name: String? = null

    ) : ResponseToolChoice

    /**
     * Use this option to force the model to call a specific custom tool.
     */
    @Serializable
    @SerialName("custom")
    public data class Custom(
        /** The name of the custom tool to call. */
        @SerialName("name") val name: String
    ) : ResponseToolChoice
}

@Serializable
public sealed interface ResponseToolChoice : ResponseToolChoiceConfig

/**
 * Serializer for [ResponseToolChoiceConfig].
 */
internal class ResponseToolChoiceSerializer :
    JsonContentPolymorphicSerializer<ResponseToolChoiceConfig>(ResponseToolChoiceConfig::class) {
    override fun selectDeserializer(element: JsonElement) = when (element) {
        is JsonPrimitive -> ResponseToolChoiceConfig.Mode.serializer()
        is JsonObject -> ResponseToolChoice.serializer()
        else -> throw IllegalArgumentException("Unknown element type: $element")
    }
}
