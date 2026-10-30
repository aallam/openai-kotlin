package com.aallam.openai.client

import com.aallam.openai.api.image.*
import com.aallam.openai.api.model.ModelId
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import io.ktor.http.content.TextContent
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.*
import kotlin.test.*

class TestOfflineImages {
    @Test
    fun generationAndCompatibilityMethodOmitLegacyFormat() = runTest {
        val engine = MockEngine { request ->
            assertEquals("/v1/images/generations", request.url.encodedPath)
            val body = Json.parseToJsonElement((request.body as TextContent).text).jsonObject
            assertEquals("gpt-image-2.5-sunburst", body["model"]!!.jsonPrimitive.content)
            assertFalse("response_format" in body)
            respond("""{"created":1,"data":[{"b64_json":"YQ=="}],"quality":"high"}""", headers = jsonHeaders)
        }
        OpenAI(OpenAIConfig(token = "offline", engine = engine)).use { client ->
            val request = ImageGenerationRequest("A tree", model = ModelId("gpt-image-2.5-sunburst"))
            assertEquals(Quality.High, client.image(request).quality)
            assertEquals("YQ==", client.imageJSON(ImageCreation(request.prompt, model = request.model)).single().b64JSON)
        }
    }

    @Test
    fun editPostsCurrentJsonReferencesWithoutMaskOrRetiredModel() = runTest {
        val engine = MockEngine { request ->
            assertEquals("/v1/images/edits", request.url.encodedPath)
            val body = Json.parseToJsonElement((request.body as TextContent).text).jsonObject
            assertFalse("model" in body)
            assertFalse("mask" in body)
            assertFalse("response_format" in body)
            assertEquals("https://example.com/input.png", body["images"]!!.jsonArray.single().jsonObject["image_url"]!!.jsonPrimitive.content)
            respond("""{"created":1,"data":[{"b64_json":"YQ=="}]}""", headers = jsonHeaders)
        }
        OpenAI(OpenAIConfig(token = "offline", engine = engine)).use { client ->
            val result = client.image(ImageEditRequest(listOf(ImageReference.url("https://example.com/input.png")), "Add a tree"))
            assertEquals("YQ==", result.data.single().b64JSON)
        }
    }

    @Test
    fun streamingPreservesPartialFinalAndErrorEvents() = runTest {
        val engine = MockEngine { request ->
            val body = Json.parseToJsonElement((request.body as TextContent).text).jsonObject
            assertEquals(true, body["stream"]!!.jsonPrimitive.boolean)
            respond(
                "data: {\"type\":\"image_generation.partial_image\",\"b64_json\":\"partial\",\"partial_image_index\":0}\n\n" +
                    "data: {\"type\":\"image_generation.completed\",\"b64_json\":\"final\"}\n\n" +
                    "data: {\"type\":\"error\",\"code\":\"test_error\",\"message\":\"Generation failed\"}\n\n",
                headers = headersOf(HttpHeaders.ContentType, "text/event-stream"),
            )
        }
        OpenAI(OpenAIConfig(token = "offline", engine = engine)).use { client ->
            val events = client.imageEvents(ImageGenerationRequest("A tree", model = ModelId("image-model"))).toList()
            assertEquals(listOf("partial", "final", null), events.map { it.b64JSON })
            assertEquals("test_error", events.last().code)
        }
    }

    @Test
    fun stoppingCollectionClosesResponseChannel() = runTest {
        val channel = ByteReadChannel("data: {\"type\":\"image_generation.partial_image\",\"b64_json\":\"first\"}\n\ndata: {\"type\":\"image_generation.completed\",\"b64_json\":\"second\"}\n\n")
        val engine = MockEngine { respond(channel, headers = headersOf(HttpHeaders.ContentType, "text/event-stream")) }
        OpenAI(OpenAIConfig(token = "offline", engine = engine)).use { client ->
            val events = client.imageEvents(ImageGenerationRequest("A tree", model = ModelId("image-model"))).take(1).toList()
            assertEquals("first", events.single().b64JSON)
            assertTrue(channel.isClosedForRead)
        }
    }

    private val jsonHeaders = headersOf(HttpHeaders.ContentType, "application/json")
}
