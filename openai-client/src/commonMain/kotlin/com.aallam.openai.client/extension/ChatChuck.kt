package com.aallam.openai.client.extension

import com.aallam.openai.api.ExperimentalOpenAI
import com.aallam.openai.api.chat.ChatChunk
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.client.extension.internal.ChatMessageAssembler

/**
 * Merges a list of [ChatChunk]s into a single consolidated [ChatMessage].
 */
@ExperimentalOpenAI
public fun List<ChatChunk>.mergeToChatMessage(): ChatMessage {
    require(isNotEmpty()) { "ChatChunks List must not be empty" }
    return fold(ChatMessageAssembler()) { assembler, chatChunk -> assembler.merge(chatChunk) }.build()
}
