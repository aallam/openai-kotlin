package com.aallam.openai.client

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionChunk
import com.aallam.openai.api.chat.ChatCompletionRequest
import io.ktor.http.*
import kotlinx.coroutines.flow.Flow

/**
 * Given a chat conversation, the model will return a chat completion response.
 */
public interface Chat {

    /**
     * Creates a completion for the chat message.
     */
    @BetaOpenAI
    public suspend fun chatCompletion(request: ChatCompletionRequest): ChatCompletion

    @BetaOpenAI
    public suspend fun chatCompletionWithHeaders(request: ChatCompletionRequest): Pair<ChatCompletion, Headers>

    /**
     * Stream variant of [chatCompletion].
     */
    @BetaOpenAI
    public fun chatCompletions(request: ChatCompletionRequest): Flow<ChatCompletionChunk>
}
