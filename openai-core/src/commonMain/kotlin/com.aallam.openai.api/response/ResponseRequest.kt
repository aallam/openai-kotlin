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
     * Build the input items using a DSL.
     */
    public fun input(block: ResponseInputBuilder.() -> Unit) {
        input = ResponseInputBuilder().apply(block).build()
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
    )
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
        items.add(ResponseInputItem.Message(role = role, content = content))
    }
    
    /**
     * Add a message input item using a builder.
     */
    public fun message(block: MessageInputBuilder.() -> Unit) {
        val builder = MessageInputBuilder().apply(block)
        items.add(ResponseInputItem.Message(
            role = requireNotNull(builder.role) { "role is required" },
            content = requireNotNull(builder.content) { "content is required" }
        ))
    }
    
    /**
     * Add a reasoning input item.
     */
    public fun reasoning(content: List<ReasoningContentPart>, summary: List<SummaryContentPart>, encryptedContent: String? = null) {
        items.add(ResponseInputItem.Reasoning(content = content, summary = summary, encryptedContent = encryptedContent))
    }
    
    /**
     * Add a reasoning input item using a builder.
     */
    public fun reasoning(block: ReasoningInputBuilder.() -> Unit) {
        val builder = ReasoningInputBuilder().apply(block)
        items.add(ResponseInputItem.Reasoning(
            content = requireNotNull(builder.content) { "content is required" },
            summary = requireNotNull(builder.summary) { "summary is required" },
            encryptedContent = builder.encryptedContent
        ))
    }
    
    internal fun build(): List<ResponseInputItem> = items.toList()
}

/**
 * Builder for message input items.
 */
@OpenAIDsl
public class MessageInputBuilder {
    /**
     * The role of the message author.
     */
    public var role: ChatRole? = null
    
    /**
     * The content of the message.
     */
    public var content: String? = null
}

/**
 * Builder for reasoning input items.
 */
@OpenAIDsl
public class ReasoningInputBuilder {
    /**
     * The reasoning content.
     */
    public var content: List<ReasoningContentPart>? = null

    /**
     * A summary of the reasoning content.
     */
    public var summary: List<SummaryContentPart>? = null

    /**
     * The encrypted reasoning content from a previous response.
     */
    public var encryptedContent: String? = null
}

/**
 * Create a [ResponseRequest] using a DSL.
 */
public fun responseRequest(block: ResponseRequestBuilder.() -> Unit): ResponseRequest =
    ResponseRequestBuilder().apply(block).build()
