package com.aallam.openai.api.response

import com.aallam.openai.api.OpenAIDsl
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request for creating a response using the responses API.
 */
@Serializable
public data class ResponseRequest(
    /**
     * ID of the model to use.
     */
    @SerialName("model") public val model: ModelId,
    
    /**
     * The input items for the response.
     */
    @SerialName("input") public val input: List<ResponseInputItem>,
    
    /**
     * Whether to store the response. Always false for stateless usage.
     */
    @EncodeDefault @SerialName("store") public val store: Boolean = false,
    
    /**
     * Configuration for reasoning behavior.
     */
    @SerialName("reasoning") public val reasoning: ReasoningConfig? = null,
    
    /**
     * Additional fields to include in the response.
     * Use ["reasoning.encrypted_content"] to get reasoning traces.
     */
    @SerialName("include") public val include: List<String>? = null,
    
    /**
     * Sampling temperature between 0 and 2.
     */
    @SerialName("temperature") public val temperature: Double? = null,
    
    /**
     * Maximum number of tokens to generate.
     */
    @SerialName("max_output_tokens") public val maxOutputTokens: Int? = null,

    /**
     * Instructions for the model on how to respond.
     */
    @SerialName("instructions") public val instructions: String? = null,
    
    /**
     * Nucleus sampling parameter.
     */
    @SerialName("top_p") public val topP: Double? = null,

    /**
     * Whether to stream the response.
     */
    @SerialName("stream") public val stream: Boolean? = null,

    @SerialName("tools") public val tools: List<Tool>? = null,
)

/**
 * Builder for [ResponseRequest].
 */
@OpenAIDsl
public class ResponseRequestBuilder {
    /**
     * ID of the model to use.
     */
    public var model: ModelId? = null
    
    /**
     * The input items for the response.
     */
    public var input: List<ResponseInputItem>? = null

    public var tools: List<Tool>? = null
    
    /**
     * Whether to store the response. Always false for stateless usage.
     */
    public var store: Boolean = false
    
    /**
     * Configuration for reasoning behavior.
     */
    public var reasoning: ReasoningConfig? = null
    
    /**
     * Additional fields to include in the response.
     */
    public var include: List<String>? = null
    
    /**
     * Sampling temperature between 0 and 2.
     */
    public var temperature: Double? = null
    
    /**
     * Maximum number of tokens to generate.
     */
    public var maxOutputTokens: Int? = null
    
    /**
     * Nucleus sampling parameter.
     */
    public var topP: Double? = null

    /**
     * Instructions for the model on how to respond.
     */
    public var instructions: String? = null
    
    /**
     * Whterh to stream the response.
     */
     public var stream: Boolean? = null
    
    /**
     * Build the input items using a DSL.
     */
    public fun input(block: ResponseInputBuilder.() -> Unit) {
        input = ResponseInputBuilder().apply(block).build()
    }

    public fun tools(block: ToolsBuilder.() -> Unit) {
        tools = ToolsBuilder().apply(block).build()
    }
    
    /**
     * Build the [ResponseRequest].
     */
    public fun build(): ResponseRequest = ResponseRequest(
        model = requireNotNull(model) { "model is required" },
        input = requireNotNull(input) { "input is required" },
        store = store,
        reasoning = reasoning,
        include = include,
        temperature = temperature,
        maxOutputTokens = maxOutputTokens,
        topP = topP,
        instructions = instructions,
        stream = stream,
        tools = tools,
    )
}

@OpenAIDsl
public class ToolsBuilder {
    private val tools = mutableListOf<Tool>()

    public fun tool(block: ToolBuilder.() -> Unit) {
        tools.add(ToolBuilder().apply(block).build())
    }

    internal fun build(): List<Tool> = tools.toList()
}

/**
 * Builder for response input items.
 */
@OpenAIDsl
public class ResponseInputBuilder {
    private val items = mutableListOf<ResponseInputItem>()
    
    /**
     * Add a message input item.
     */
    public fun message(role: ChatRole, content: String) {
        val status = if (role == ChatRole.Assistant) MessageStatus.Completed else null
        val messageContent = if (role == ChatRole.Assistant) {
            MessageContent.OutputText(content)
        } else {
            MessageContent.InputText(content)
        }
        items.add(
            Message(
                role = role,
                content = listOf(messageContent),
                status = status,
            ),
        )
    }
    
    /**
     * Add a message input item using a builder.
     */
    public fun message(block: MessageBuilder.() -> Unit) {
        items.add(MessageBuilder().apply(block).build())
    }
    
    /**
     * Add a reasoning input item.
     */
    public fun reasoning(block: ReasoningBuilder.() -> Unit) {
        items.add(ReasoningBuilder().apply(block).build())
    }

    public fun functionCall(block: FunctionCallBuilder.() -> Unit) {
        items.add(FunctionCallBuilder().apply(block).build())
    }

    public fun functionCallOutput(block: FunctionCallOutputBuilder.() -> Unit) {
        items.add(FunctionCallOutputBuilder().apply(block).build())
    }

    /**
     * Add a tool input item.
     */
    
    internal fun build(): List<ResponseInputItem> = items.toList()
}


/**
 * Create a [ResponseRequest] using a DSL.
 */
public fun responseRequest(block: ResponseRequestBuilder.() -> Unit): ResponseRequest =
    ResponseRequestBuilder().apply(block).build()
