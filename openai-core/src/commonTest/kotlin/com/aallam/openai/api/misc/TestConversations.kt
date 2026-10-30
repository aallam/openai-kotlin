package com.aallam.openai.api.misc

import com.aallam.openai.api.conversation.*
import com.aallam.openai.api.core.PaginatedList
import com.aallam.openai.api.core.Role
import kotlinx.serialization.json.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class TestConversations {
    @Test
    fun messagesAndToolOutputsHaveApiItemShapes() {
        val request = ConversationRequest(items = listOf(
            ConversationItem.message(Role.User, "Hello"),
            ConversationItem.functionOutput("call_1", "42"),
        ), metadata = mapOf("topic" to "demo"))
        val encoded = Json.encodeToJsonElement(ConversationRequest.serializer(), request).jsonObject
        assertEquals(Json.parseToJsonElement("""[{"type":"message","role":"user","content":"Hello"},{"type":"function_call_output","call_id":"call_1","output":"42"}]"""), encoded["items"])
        assertEquals("demo", encoded["metadata"]?.jsonObject?.get("topic")?.jsonPrimitive?.content)
    }

    @Test
    fun hostedToolItemsRetainUnknownFieldsAndPagination() {
        val payload = """{"data":[{"id":"mcp_1","type":"mcp_call","server_label":"docs","output":{"custom":[1,2]},"extra":true}],"first_id":"mcp_1","last_id":"mcp_1","has_more":true}"""
        val page = Json.decodeFromString(PaginatedList.serializer(ConversationItem.serializer()), payload)
        assertEquals("mcp_1", page.firstId)
        assertEquals(true, page.hasMore)
        assertEquals("mcp_call", page.first().type)
        val encodedItem = Json.encodeToJsonElement(ConversationItem.serializer(), page.first())
        assertEquals(Json.parseToJsonElement(payload).jsonObject["data"]!!.jsonArray.first(), encodedItem)
    }

    @Test
    fun contentPartsAndClearedMetadataArePreserved() {
        val content = Json.parseToJsonElement("""[{"type":"input_file","file_id":"file_1"}]""")
        val item = ConversationItem.message(Role.User, content)
        val decoded = Json.decodeFromString(ConversationItem.serializer(), Json.encodeToString(ConversationItem.serializer(), item))
        assertEquals(content, decoded.content)
        assertEquals(Role.User, decoded.role)
        assertEquals("""{"metadata":null}""", Json.encodeToString(ConversationUpdateRequest.serializer(), ConversationUpdateRequest(null)))
    }

    @Test
    fun rejectsOversizedItemBatches() {
        val items = List(21) { ConversationItem.message(Role.User, "Hello") }
        assertFailsWith<IllegalArgumentException> { ConversationRequest(items) }
        assertFailsWith<IllegalArgumentException> { ConversationItemsRequest(items) }
    }
}
