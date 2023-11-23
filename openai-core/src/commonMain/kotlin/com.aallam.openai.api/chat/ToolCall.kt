package com.aallam.openai.api.chat

import com.aallam.openai.api.OpenAIDsl
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Details of the tool call.
 */
@Serializable
public sealed interface ToolCall {

    /**
     * Tool call.
     * [index] is required in the case of chat stream variant.
     */
    @Serializable
    @SerialName("function")
    public data class Function(
        /** Tool call index. Required in the case of chat stream variant **/
        @SerialName("index") val index: Int? = null,
        /** The ID of the tool call. **/
        @SerialName("id") val id: ToolId? = null,
        /** The function that the model called. **/
        @SerialName("function") val function: FunctionCall? = null,
    ) : ToolCall
}

/**
 * Tool: function call.
 */
public fun function(block: FunctionToolCallBuilder.() -> Unit): ToolCall.Function =
    FunctionToolCallBuilder().apply(block).build()


/**
 * Tool call builder.
 */
@OpenAIDsl
public class FunctionToolCallBuilder {

    /** Tool call index. Required in the case of chat stream variant **/
    public var index: Int? = null

    /** The ID of the tool call. **/
    public var id: ToolId? = null

    /** The function that the model called. **/
    public var function: FunctionCall? = null

    /**
     * Create [ToolCall] instance.
     */
    public fun build(): ToolCall.Function = ToolCall.Function(
        index = index,
        id = id,
        function = function,
    )
}
