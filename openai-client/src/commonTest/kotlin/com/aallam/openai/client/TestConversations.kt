package com.aallam.openai.client

import com.aallam.openai.api.conversation.ConversationItem
import com.aallam.openai.api.conversation.ConversationItemsRequest
import com.aallam.openai.api.conversation.ConversationRequest
import com.aallam.openai.api.conversation.ConversationUpdateRequest
import com.aallam.openai.api.core.Role
import com.aallam.openai.client.internal.withTestCleanup
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TestConversations : TestOpenAI() {
    @Test
    fun conversationAndMessageLifecycle() = test {
        withTestCleanup {
            val conversation = openAI.conversation(ConversationRequest(metadata = mapOf("test" to "lifecycle")))
            var deleted = false
            afterTest { if (!deleted) assertTrue(openAI.delete(conversation.id)) }
            assertEquals(conversation, openAI.conversation(conversation.id))

            val metadata = mapOf("modified" to "true")
            assertEquals(metadata, openAI.updateConversation(conversation.id, ConversationUpdateRequest(metadata)).metadata)

            val items = openAI.createConversationItems(conversation.id,
                ConversationItemsRequest(listOf(ConversationItem.message(Role.User, "Hello."))))
            val message = items.single()
            val messageId = assertNotNull(message.id)
            var messageDeleted = false
            afterTest { if (!messageDeleted) openAI.deleteConversationItem(conversation.id, messageId) }
            assertEquals(Role.User, message.role)
            assertEquals("message", message.type)
            val text = when (val content = message.content) {
                is JsonPrimitive -> content.content
                is JsonArray -> content.single().jsonObject.getValue("text").jsonPrimitive.content
                else -> error("Expected text message content")
            }
            assertEquals("Hello.", text)
            assertEquals(message, openAI.conversationItem(conversation.id, messageId))
            assertEquals(messageId, openAI.conversationItems(conversation.id).single().id)

            assertEquals(conversation.id, openAI.deleteConversationItem(conversation.id, messageId).id)
            messageDeleted = true
            assertNull(openAI.conversationItem(conversation.id, messageId))
            assertTrue(openAI.conversationItems(conversation.id).isEmpty())
            assertTrue(openAI.delete(conversation.id))
            deleted = true
            assertNull(openAI.conversation(conversation.id))
        }
    }
}
