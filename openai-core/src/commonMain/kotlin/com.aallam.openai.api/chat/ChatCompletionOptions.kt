package com.aallam.openai.api.chat

import kotlinx.serialization.*

/** Advanced fields whose types differ from the original Chat request contract. */
@Serializable
public data class ChatCompletionOptions(
    /** Replaces request messages with richer multimodal messages. */
    public val messages: List<ChatInputMessage>? = null,
    /** Replaces request tools, allowing custom text tools. */
    public val tools: List<ChatTool>? = null,
    /** Replaces the original function-only tool choice. */
    @SerialName("tool_choice") public val toolChoice: ChatToolSelection? = null,
)

/** Select a custom tool or an allowed tool subset. */
@Serializable
public data class ChatToolSelection(
    /** custom or allowed_tools. */
    public val type: String,
    /** Named custom tool. */
    public val custom: FunctionToolChoice? = null,
    /** Restricted selection policy. */
    @SerialName("allowed_tools") public val allowedTools: ChatAllowedTools? = null,
)

/** Allowed subset of tools. */
@Serializable
public data class ChatAllowedTools(
    /** auto or required. */
    public val mode: String,
    /** Tool references including their names. */
    public val tools: List<ChatAllowedTool>,
)

/** A function or custom tool reference. */
@Serializable
public data class ChatAllowedTool(
    /** function or custom. */
    public val type: String,
    /** Function tool reference. */
    public val function: FunctionToolChoice? = null,
    /** Custom tool reference. */
    public val custom: FunctionToolChoice? = null,
)
