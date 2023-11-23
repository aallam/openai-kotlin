package com.aallam.openai.client.extension.internal

import com.aallam.openai.api.chat.*

/**
 * A class to help assemble chat messages from chat chunks.
 */
internal class ChatMessageAssembler {
    private val chatFuncName = StringBuilder()
    private val chatFuncArgs = StringBuilder()
    private val chatContent = StringBuilder()
    private var chatRole: ChatRole? = null
    private val toolCallsAssemblers = mutableMapOf<Int, ToolCallAssembler>()

    /**
     * Merges a chat chunk into the chat message being assembled.
     */
    fun merge(chunk: ChatChunk): ChatMessageAssembler {
        chunk.delta.run {
            role?.let { chatRole = it }
            content?.let { chatContent.append(it) }
            functionCall?.let { call ->
                call.nameOrNull?.let { chatFuncName.append(it) }
                call.argumentsOrNull?.let { chatFuncArgs.append(it) }
            }
            toolCalls?.onEach { toolCall ->
                toolCall as? ToolCall.Function ?: error("Tool call is not a function")
                val index = toolCall.index ?: error("index is required in case of tool calls from chat stream variant")
                val assembler = toolCallsAssemblers.getOrPut(index) { ToolCallAssembler() }
                assembler.merge(toolCall)
            }
        }
        return this
    }

    /**
     * Builds and returns the assembled chat message.
     */
    fun build(): ChatMessage = chatMessage {
        this.role = chatRole
        this.content = chatContent.toString()
        if (chatFuncName.isNotEmpty() || chatFuncArgs.isNotEmpty()) {
            this.functionCall = FunctionCall(chatFuncName.toString(), chatFuncArgs.toString())
            this.name = chatFuncName.toString()
        }
        if (toolCallsAssemblers.isNotEmpty()) {
            this.toolCalls = toolCallsAssemblers.map { (_, value) -> value.build() }.toList()
        }
    }
}

internal class ToolCallAssembler {
    private var toolIndex: Int? = null
    private var toolId: ToolId? = null
    private var funcName: String? = null
    private val funcArgs = StringBuilder()

    fun merge(toolCall: ToolCall.Function): ToolCallAssembler {
        toolCall.index?.let { toolIndex = it }
        toolCall.id?.let { toolId = it }
        toolCall.function?.let { call ->
            call.nameOrNull?.let { funcName = it }
            call.argumentsOrNull?.let { funcArgs.append(it) }
        }
        return this
    }

    /**
     * Builds and returns the assembled chat message.
     */
    fun build(): ToolCall = function {
        this.index = toolIndex
        this.id = toolId
        if (funcName?.isNotEmpty() == true || funcArgs.isNotEmpty()) {
            this.function = FunctionCall(funcName, funcArgs.toString())
        }
    }
}
