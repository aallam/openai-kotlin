package com.aallam.openai.client

import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.embedding.*
import com.aallam.openai.api.model.ModelId
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.*
import kotlin.test.*

class TestOfflineEmbeddingFormats {
    @Test
    fun typedTokenInputsAndBothVectorEncodingsRetainMetadata() = runTest {
        val engine = MockEngine { request ->
            assertEquals("/v1/embeddings", request.url.encodedPath)
            assertEquals("request-123", request.headers["X-Client-Request-Id"])
            val wire = Json.parseToJsonElement(request.body.toByteArray().decodeToString()).jsonObject
            assertEquals(Json.parseToJsonElement("[[12],[34,56]]"), wire["input"])
            assertEquals(4, wire["dimensions"]?.jsonPrimitive?.int)
            val vector = if (wire["encoding_format"]?.jsonPrimitive?.content == "base64") "\"AACAPwAAIMAAAAAAAAAAgA==\"" else "[1,-2.5,0,-0.0]"
            respond("""{"object":"list","model":"server-model","data":[{"object":"embedding","index":0,"embedding":$vector}],"usage":{"prompt_tokens":3,"total_tokens":3}}""", HttpStatusCode.OK, headersOf(HttpHeaders.ContentType, "application/json"))
        }
        OpenAI(OpenAIConfig(token = "offline-token", engine = engine)).use { api ->
            for (format in EmbeddingEncodingFormat.entries) {
                val request = EmbeddingCreateRequest(ModelId("model"), EmbeddingInput.TokenBatch(listOf(listOf(12), listOf(34, 56))), 4, encodingFormat = format)
                val result = api.createEmbeddings(request, RequestOptions(headers = mapOf("X-Client-Request-Id" to "request-123")))
                assertEquals("server-model", result.model.id)
                assertEquals("list", result.objectType)
                assertEquals("embedding", result.embeddings.single().objectType)
                assertEquals(3, result.usage.totalTokens)
                assertEquals(listOf(1.0, -2.5, 0.0, -0.0), result.embeddings.single().embedding.values())
            }
        }
    }
}

/** Legacy implementers remain valid without implementing the new overload. */
private class LegacyEmbeddingsImplementation : Embeddings {
    override suspend fun embeddings(request: EmbeddingRequest, requestOptions: RequestOptions?): EmbeddingResponse =
        error("Unused source compatibility fixture")
}
