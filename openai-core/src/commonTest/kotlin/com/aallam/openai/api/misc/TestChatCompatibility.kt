package com.aallam.openai.api.misc

import com.aallam.openai.api.chat.*
import com.aallam.openai.api.model.ModelId
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class TestChatCompatibility {

    @Test
    fun chatCompletionBuilderSetsNewConfigurableFields() {
        val request = chatCompletionRequest {
            model = ModelId("gpt-4o-mini")
            messages {
                user { content = "hello" }
            }
            reasoningEffort = Effort("high")
            store = true
            maxCompletionTokens = 256
            webSearchOptions = WebSearchOptions(
                searchContextSize = SearchContextSize.Medium,
                userLocation = UserLocation(
                    approximate = ApproximateLocation(
                        city = "San Francisco",
                        country = "US",
                        region = "California",
                        timezone = "America/Los_Angeles"
                    )
                )
            )
        }

        val encoded = Json.encodeToJsonElement(ChatCompletionRequest.serializer(), request).jsonObject
        assertEquals("high", encoded["reasoning_effort"]?.jsonPrimitive?.content)
        assertEquals(true, encoded["store"]?.jsonPrimitive?.content?.toBooleanStrictOrNull())
        assertEquals(256, encoded["max_completion_tokens"]?.jsonPrimitive?.content?.toInt())

        val webSearch = encoded["web_search_options"]?.jsonObject
        assertNotNull(webSearch)
        assertEquals("medium", webSearch["search_context_size"]?.jsonPrimitive?.content)
    }

    @Test
    fun chatResponseFormatSupportsPrimitiveAndObjectVariants() {
        val primitive = Json.decodeFromString(
            ChatResponseFormat.serializer(),
            "\"json_object\""
        )
        assertEquals("json_object", primitive.type)

        val fromObject = Json.decodeFromString(
            ChatResponseFormat.serializer(),
            """{"type":"json_schema","json_schema":{"name":"Result","schema":{"type":"object"}}}"""
        )
        assertEquals("json_schema", fromObject.type)
        assertEquals("Result", fromObject.jsonSchema?.name)

        val encoded = Json.encodeToString(ChatResponseFormat.serializer(), ChatResponseFormat.Text)
        assertEquals("""{"type":"text"}""", encoded)
    }

    @Test
    fun contentPartUsesTypeDiscriminator() {
        val payload = """
            {
              "role":"user",
              "content":[
                {"type":"text","text":"Hello"},
                {"type":"image_url","image_url":{"url":"https://example.com/image.png"}}
              ]
            }
        """.trimIndent()
        val message = Json.decodeFromString(ChatMessage.serializer(), payload)
        assertTrue(message.messageContent is ListContent)
    }

    @Test
    fun chatMessageIncludesAnnotationsAndReasoningContent() {
        val payload = """
            {
              "role":"assistant",
              "content":"Response",
              "annotations":[
                {"type":"url_citation","url":"https://example.com","title":"Example","start_index":0,"end_index":8}
              ],
              "reasoning_content":"hidden reasoning"
            }
        """.trimIndent()

        val message = Json.decodeFromString(ChatMessage.serializer(), payload)
        assertEquals("hidden reasoning", message.reasoningContent)
        assertEquals("url_citation", message.annotations?.firstOrNull()?.type)
    }
}
