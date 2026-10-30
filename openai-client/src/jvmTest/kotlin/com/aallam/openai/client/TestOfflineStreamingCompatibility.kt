package com.aallam.openai.client

import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatCompletionChunk
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.api.exception.OpenAIServerException
import com.aallam.openai.client.internal.api.ChatApi
import com.aallam.openai.client.internal.createHttpClient
import com.aallam.openai.client.internal.extension.streamEventsFrom
import com.aallam.openai.client.internal.http.HttpRequester
import com.aallam.openai.client.internal.http.HttpTransport
import io.ktor.client.engine.mock.*
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.*
import io.ktor.utils.io.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFailsWith

class TestOfflineStreamingCompatibility {
    @Test
    fun flowCollectorAndChannelProducersShareTheEventDecoder() = runTest {
        val engine = MockEngine {
            respond(
                "data: {\"id\":\"chat_1\",\"created\":0,\"model\":\"test\",\"choices\":[]}\n\ndata: [DONE]\n\n",
                headers = headersOf(HttpHeaders.ContentType, "text/event-stream"),
            )
        }
        createHttpClient(OpenAIConfig("test", engine = engine, logging = LoggingConfig(LogLevel.None))).use { client ->
            val response = client.get("/chat/completions")
            val chunks = flow<ChatCompletionChunk> { streamEventsFrom(response) }.toList()
            assertEquals(listOf("chat_1"), chunks.map { it.id })
        }
    }

    @Test
    fun actualTransportStreamsOnConfiguredEngineDispatcher() = runTest {
        val engine = MockEngine {
            respond(
                "data: {\"id\":\"chat_1\",\"created\":0,\"model\":\"test\",\"choices\":[]}\n\ndata: [DONE]\n\n",
                headers = headersOf(HttpHeaders.ContentType, "text/event-stream"),
            )
        }
        OpenAI(OpenAIConfig("test", engine = engine, logging = LoggingConfig(LogLevel.None))).use { client ->
            val chunks = client.chatCompletions(ChatCompletionRequest(ModelId("test"), emptyList())).toList()
            assertEquals(listOf("chat_1"), chunks.map { it.id })
        }
    }

    @Test
    fun stoppingCollectionCancelsTheOpenResponseChannel() = runTest {
        val body = ByteChannel(autoFlush = true)
        body.writeStringUtf8("data: {\"id\":\"chat_1\",\"created\":0,\"model\":\"test\",\"choices\":[]}\n\n")
        val engine = MockEngine {
            respond(body, headers = headersOf(HttpHeaders.ContentType, "text/event-stream"))
        }
        OpenAI(OpenAIConfig("test", engine = engine, logging = LoggingConfig(LogLevel.None))).use { client ->
            val chunks = client.chatCompletions(ChatCompletionRequest(ModelId("test"), emptyList())).take(1).toList()
            assertEquals(1, chunks.size)
            assertTrue(body.isClosedForRead)
        }
    }

    @Test
    fun streamingHttpErrorsStillPropagate() = runTest {
        val engine = MockEngine {
            respond("server error", HttpStatusCode.InternalServerError)
        }
        OpenAI(OpenAIConfig("test", engine = engine, logging = LoggingConfig(LogLevel.None))).use { client ->
            assertFailsWith<OpenAIServerException> {
                client.chatCompletions(ChatCompletionRequest(ModelId("test"), emptyList())).toList()
            }
        }
    }

    @Test
    fun chatStreamingAllowsTransportToExecuteOnEngineDispatcher() = runTest {
        val engine = MockEngine {
            respond(
                "data: {\"id\":\"chat_1\",\"created\":0,\"model\":\"test\",\"choices\":[]}\n\ndata: [DONE]\n\n",
                headers = headersOf(HttpHeaders.ContentType, "text/event-stream"),
            )
        }
        val transport = HttpTransport(createHttpClient(OpenAIConfig("test", engine = engine, logging = LoggingConfig(LogLevel.None))))
        val requester = object : HttpRequester by transport {
            override suspend fun <T : Any> perform(builder: HttpRequestBuilder, block: suspend (HttpResponse) -> T) {
                // Ktor 3.4+ can execute the response callback on its engine dispatcher.
                withContext(Dispatchers.IO) { transport.perform(builder, block) }
            }
        }
        try {
            val chunks = ChatApi(requester).chatCompletions(ChatCompletionRequest(ModelId("test"), emptyList()), null).toList()
            assertEquals(listOf("chat_1"), chunks.map { it.id })
        } finally {
            transport.close()
        }
    }
}
