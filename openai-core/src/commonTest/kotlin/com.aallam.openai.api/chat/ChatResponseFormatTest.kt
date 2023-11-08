package com.aallam.openai.api.chat

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class ChatResponseFormatTest {

    @Test
    fun serialize() {
        listOf(
            ChatResponseFormat.JSON,
            ChatResponseFormat.TEXT,
        ).forEach {
            val jsonString = Json.encodeToString(ChatResponseFormat.serializer(), it)
            assertEquals(jsonString, """{"type":"${it.value}"}""")
            val decoded = Json.decodeFromString(ChatResponseFormat.serializer(), jsonString)
            assertEquals(it, decoded)
        }
    }
}
