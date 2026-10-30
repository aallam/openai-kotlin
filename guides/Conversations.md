# Conversations

Conversations persist context independently of any one model response. Create a conversation,
add items, and use its ID when creating a Responses API request that supports conversation linkage.

```kotlin
val conversation = openAI.conversation(
    ConversationRequest(
        items = listOf(ConversationItem.message(Role.User, "Hello")),
        metadata = mapOf("topic" to "onboarding"),
    )
)

val page = openAI.conversationItems(conversation.id, limit = 20)
val nextPage = if (page.hasMore == true) {
    openAI.conversationItems(conversation.id, after = page.lastId)
} else null

openAI.createConversationItems(
    conversation.id,
    ConversationItemsRequest(listOf(ConversationItem.message(Role.User, "Tell me more"))),
)
openAI.updateConversation(conversation.id, ConversationUpdateRequest(metadata = null))
openAI.deleteConversationItem(conversation.id, page.first().id!!)
openAI.delete(conversation.id)
```

The relevant types live in `com.aallam.openai.api.conversation`; `Role` is in
`com.aallam.openai.api.core`. Create/add requests accept at most 20 items. Item retrieval,
creation and listing accept `include` values to request extra data such as encrypted reasoning.
Deleting an item returns the updated conversation. Deleting a conversation does not delete
its items.

`ConversationItem.message` supports text and multimodal content arrays;
`ConversationItem.functionOutput` submits a tool result. `ConversationItem(JsonObject)` accepts
other item variants. The complete object is retained in `value`, including hosted-tool fields
that are not exposed as convenience properties, so item history can be round-tripped safely.

Persistent conversations are optional. Applications may instead pass explicit input history
using `ResponseInput`, or chain Responses with `previousResponseId`.

See the [official Conversations reference](https://developers.openai.com/api/reference/resources/conversations).
