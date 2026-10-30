package com.aallam.openai.client

import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.api.moderation.ModerationInput
import com.aallam.openai.api.moderation.ModerationRequest
import com.aallam.openai.api.moderation.MultimodalModerationRequest
import com.aallam.openai.api.moderation.ModerationModel
import com.aallam.openai.api.moderation.TextModeration
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.toByteArray
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class TestOfflineModeration {
    @Test
    fun legacyInferredModerationReferenceRemainsUnambiguous() = runTest {
        val client = object : Moderations {
            override suspend fun moderations(request: ModerationRequest, requestOptions: RequestOptions?): TextModeration {
                assertEquals(listOf("check"), request.input)
                return TextModeration("modr_legacy", ModerationModel.OmniLatest, emptyList())
            }
        }
        val reference = client::moderations
        assertEquals("modr_legacy", reference(ModerationRequest(listOf("check")), null).id)
    }

    @Test
    fun existingTextOnlyClientStillCompiles() = runTest {
        val client = object : Moderations {
            override suspend fun moderations(request: ModerationRequest, requestOptions: RequestOptions?): TextModeration =
                throw UnsupportedOperationException("Text-only test client")
        }
        assertFailsWith<UnsupportedOperationException> {
            client.moderate(MultimodalModerationRequest(listOf(ModerationInput.Text("check"))))
        }
    }

    @Test
    fun multimodalRequestReachesModerationsWithRequestOptions() = runTest {
        val engine = MockEngine { request ->
            assertEquals(HttpMethod.Post, request.method)
            assertEquals("/v1/moderations", request.url.encodedPath)
            assertEquals("test", request.headers["X-Request-Marker"])
            assertEquals(
                Json.parseToJsonElement("""{"input":[{"type":"text","text":"check"},{"type":"image_url","image_url":{"url":"https://example.com/image.png"}}]}"""),
                Json.parseToJsonElement(request.body.toByteArray().decodeToString()),
            )
            respond(
                """{"id":"modr_123","model":"omni-moderation-latest","results":[]}""",
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val client = OpenAI(OpenAIConfig(
            token = "test-token",
            engine = engine,
            logging = LoggingConfig(logLevel = LogLevel.None),
        ))
        try {
            val result = client.moderate(
                MultimodalModerationRequest(listOf(
                    ModerationInput.Text("check"),
                    ModerationInput.Image("https://example.com/image.png"),
                )),
                RequestOptions(headers = mapOf("X-Request-Marker" to "test")),
            )
            assertEquals("modr_123", result.id)
            assertEquals("omni-moderation-latest", result.model.model)
        } finally {
            client.close()
        }
    }
}
