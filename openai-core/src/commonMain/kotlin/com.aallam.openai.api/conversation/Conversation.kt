package com.aallam.openai.api.conversation

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/** Identifier of a persisted conversation. */
@Serializable
@JvmInline
public value class ConversationId(public val id: String)

/** A persisted conversation. Items are accessed separately through the Conversations API. */
@Serializable
public data class Conversation(
    @SerialName("id") public val id: ConversationId,
    @SerialName("created_at") public val createdAt: Long,
    @SerialName("metadata") public val metadata: Map<String, String>? = null,
    @SerialName("object") public val objectType: String = "conversation",
)

/** Creates a conversation with optional initial items and metadata. At most 20 items may be supplied. */
@Serializable
public data class ConversationRequest(
    @SerialName("items") public val items: List<ConversationItem>? = null,
    @SerialName("metadata") public val metadata: Map<String, String>? = null,
) {
    init {
        require(items == null || items.size <= 20) { "At most 20 conversation items may be supplied" }
    }
}

/** Replaces conversation metadata. Passing null clears existing metadata. */
@Serializable
public data class ConversationUpdateRequest(
    @SerialName("metadata") public val metadata: Map<String, String>?,
)

/** Adds up to 20 items to a conversation. */
@Serializable
public data class ConversationItemsRequest(
    @SerialName("items") public val items: List<ConversationItem>,
) {
    init {
        require(items.size <= 20) { "At most 20 conversation items may be supplied" }
    }
}
