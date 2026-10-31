package com.aallam.openai.client

import com.aallam.openai.api.conversation.*
import com.aallam.openai.api.core.PaginatedList
import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.core.SortOrder

/**
 * Manage persisted conversations and their items. Deleting a conversation does not delete its items.
 *
 * Default implementations throw [UnsupportedOperationException] so existing custom [OpenAI]
 * implementations can remain source compatible without implementing this optional capability.
 * Clients created by the SDK implement all operations.
 */
public interface Conversations {
    /** Creates a conversation, optionally with initial items. */
    public suspend fun conversation(
        request: ConversationRequest = ConversationRequest(),
        requestOptions: RequestOptions? = null,
    ): Conversation = throw UnsupportedOperationException("Conversations are not supported by this client.")

    /** Retrieves a conversation, or null when it does not exist. */
    public suspend fun conversation(
        id: ConversationId,
        requestOptions: RequestOptions? = null,
    ): Conversation? = throw UnsupportedOperationException("Conversations are not supported by this client.")

    /** Updates the supplied metadata keys, preserving existing keys that are not included. */
    public suspend fun updateConversation(
        id: ConversationId,
        request: ConversationUpdateRequest,
        requestOptions: RequestOptions? = null,
    ): Conversation = throw UnsupportedOperationException("Conversations are not supported by this client.")

    /** Deletes a conversation. Returns false when it does not exist. */
    public suspend fun delete(
        id: ConversationId,
        requestOptions: RequestOptions? = null,
    ): Boolean = throw UnsupportedOperationException("Conversations are not supported by this client.")

    /** Adds up to 20 items and returns the created items with pagination metadata. */
    public suspend fun createConversationItems(
        id: ConversationId,
        request: ConversationItemsRequest,
        include: List<String>? = null,
        requestOptions: RequestOptions? = null,
    ): PaginatedList<ConversationItem> = throw UnsupportedOperationException("Conversations are not supported by this client.")

    /** Retrieves an item, or null when it does not exist. Include requests additional item data. */
    public suspend fun conversationItem(
        id: ConversationId,
        itemId: String,
        include: List<String>? = null,
        requestOptions: RequestOptions? = null,
    ): ConversationItem? = throw UnsupportedOperationException("Conversations are not supported by this client.")

    /** Removes an item and returns the updated conversation. */
    public suspend fun deleteConversationItem(
        id: ConversationId,
        itemId: String,
        requestOptions: RequestOptions? = null,
    ): Conversation = throw UnsupportedOperationException("Conversations are not supported by this client.")

    /** Lists conversation items. Pass lastId as after to request the next page. */
    public suspend fun conversationItems(
        id: ConversationId,
        limit: Int? = null,
        order: SortOrder? = null,
        after: String? = null,
        include: List<String>? = null,
        requestOptions: RequestOptions? = null,
    ): PaginatedList<ConversationItem> = throw UnsupportedOperationException("Conversations are not supported by this client.")
}
