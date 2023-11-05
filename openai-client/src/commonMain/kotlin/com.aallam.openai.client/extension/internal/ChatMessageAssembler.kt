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
    }
}
