package com.aallam.openai.client

import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionChunk
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatCompletionOptions
import com.aallam.openai.api.chat.ChatCompletionResult
import com.aallam.openai.api.chat.ChatResultMessage
import com.aallam.openai.api.chat.ChatCompletionListQuery
import com.aallam.openai.api.chat.ChatCompletionUpdate
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.core.PaginatedList
import com.aallam.openai.api.core.SortOrder
import com.aallam.openai.api.core.RequestOptions
import kotlinx.coroutines.flow.Flow

/**
 * Given a chat conversation, the model will return a chat completion response.
 */
public interface Chat {

    /**
     * Creates a completion for the chat message.
     *
     * @param request completion request.
     * @param requestOptions request options.
     */
    public suspend fun chatCompletion(
        request: ChatCompletionRequest,
        requestOptions: RequestOptions? = null
    ): ChatCompletion

    /**
     * Stream variant of [chatCompletion].
     *
     * @param request completion request.
     * @param requestOptions request options.
     */
    public fun chatCompletions(
        request: ChatCompletionRequest,
        requestOptions: RequestOptions? = null
    ): Flow<ChatCompletionChunk>

    /** Retrieve a stored completion; returns null if it does not exist. */
    public suspend fun storedChatCompletion(id: String, requestOptions: RequestOptions? = null): ChatCompletionResult? = throw UnsupportedOperationException("Stored completions are not supported by this implementation")

    /** List stored completions with pagination and optional filters. */
    public suspend fun storedChatCompletions(
        query: ChatCompletionListQuery = ChatCompletionListQuery(),
        requestOptions: RequestOptions? = null,
    ): PaginatedList<ChatCompletionResult> = throw UnsupportedOperationException("Stored completions are not supported by this implementation")

    /** Update metadata on a stored completion. */
    public suspend fun updateChatCompletion(
        id: String,
        request: ChatCompletionUpdate,
        requestOptions: RequestOptions? = null,
    ): ChatCompletionResult = throw UnsupportedOperationException("Stored completions are not supported by this implementation")

    /** Delete a stored completion; returns false if it does not exist. */
    public suspend fun deleteChatCompletion(id: String, requestOptions: RequestOptions? = null): Boolean = throw UnsupportedOperationException("Stored completions are not supported by this implementation")

    /** List messages belonging to a stored completion. */
    public suspend fun chatCompletionMessages(
        id: String,
        after: String? = null,
        limit: Int? = null,
        order: SortOrder? = null,
        requestOptions: RequestOptions? = null,
    ): PaginatedList<ChatResultMessage> = throw UnsupportedOperationException("Stored completions are not supported by this implementation")
    /** Create a completion using richer content and custom tool declarations. */
    public suspend fun createChatCompletion(
        request: ChatCompletionRequest,
        options: ChatCompletionOptions,
        requestOptions: RequestOptions? = null,
    ): ChatCompletionResult = throw UnsupportedOperationException("Advanced Chat is not supported by this implementation")

    /** Stream richer message and custom tool deltas. */
    public fun streamChatCompletions(
        request: ChatCompletionRequest,
        options: ChatCompletionOptions,
        requestOptions: RequestOptions? = null,
    ): Flow<ChatCompletionResult> = throw UnsupportedOperationException("Advanced Chat is not supported by this implementation")
}
