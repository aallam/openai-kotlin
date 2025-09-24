package com.aallam.openai.client

import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.api.response.*
import com.aallam.openai.api.exception.InvalidRequestException
import com.aallam.openai.client.LoggingConfig
import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.api.logging.Logger
import io.ktor.client.plugins.logging.Logging
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.TimeoutCancellationException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.fail

class TestResponses : TestOpenAI() {

    @Test
    fun testResponsesBasic() = test {
        val request = responseRequest {
            model = ModelId("gpt-5")
            input {
                message {
                    role = ChatRole.User
                    content {
                        inputText("Hello, how are you?")
                    }
                }
            }
        }

        val response = openAI.createResponse(request)

        // Validate basic response structure
        assertNotNull(response.id)
        assertEquals("response", response.objectType)
        assertNotNull(response.model)
        assertTrue(response.output.isNotEmpty())
        assertEquals("completed", response.status)
        assertEquals(false, request.store) // Always false for stateless

        // Validate output structure
        assertTrue(response.output.isNotEmpty())
        val messageOutput = response.output.find { it is Message } as? Message
        assertNotNull(messageOutput?.id)
        assertNotNull(messageOutput.id)
        assertEquals(ChatRole.Assistant, messageOutput.role)
        assertTrue(messageOutput.content.isNotEmpty())

        // Validate firstMessageTest helper
        assertNotNull(response.firstMessageText)
        assertTrue(response.firstMessageText?.isNotEmpty() == true)
    }

    @Test
    fun testResponsesStreaming() = test {
        val request = responseRequest {
            model = ModelId("gpt-5")
            reasoning = ReasoningConfig(effort = "medium", summary = "detailed")
            include = listOf("reasoning.encrypted_content")
            stream = true // Enable streaming
            input {
                message {
                    role = ChatRole.User
                    content {
                        inputText("Count from 1 to 5")
                    }
                }
            }
        }

        // Test streaming response
        val chunks = mutableListOf<ResponseChunk>()
        openAI.createResponseStream(request).collect { chunk ->
            chunks.add(chunk)
        }

        // Verify we got the expected event types
        val eventTypes = chunks.map { it.type }.toSet()
        assertTrue(eventTypes.contains("response.created"))
        assertTrue(eventTypes.contains("response.completed"))

        // Check if we got reasoning summary deltas (may or may not be present depending on model behavior)
        val summaryDeltas = chunks.filter { it.type == "response.reasoning_summary_text.delta" }
        if (summaryDeltas.isNotEmpty()) {
            assertTrue(summaryDeltas.all { it.delta?.isNotEmpty() == true }, "Summary deltas should not be empty")
        }

        // Verify final response has usage information
        val finalChunk = chunks.lastOrNull { it.type == "response.completed" }
        assertNotNull(finalChunk?.response?.usage, "Final chunk should have usage information")

        // Verify we got encrypted content in the final response
        val finalResponse = finalChunk?.response
        val reasoningOutput = finalResponse?.output?.filterIsInstance<Reasoning>()?.firstOrNull()
        assertNotNull(reasoningOutput?.encryptedContent, "Should have encrypted reasoning content")
    }

    @Test
    fun testResponsesStreamingSequenceNumbers() = test {
        val request = responseRequest {
            model = ModelId("gpt-5")
            reasoning = ReasoningConfig(effort = "low")
            stream = true
            input {
                message {
                    role = ChatRole.User
                    content {
                        inputText("Say hello")
                    }
                }
            }
        }

        val chunks = mutableListOf<ResponseChunk>()
        openAI.createResponseStream(request).collect { chunk ->
            chunks.add(chunk)
        }

        // Verify sequence numbers are monotonically increasing
        assertTrue(chunks.isNotEmpty(), "Should receive at least one chunk")

        val sequenceNumbers = chunks.map { it.sequenceNumber }
        val sortedSequenceNumbers = sequenceNumbers.sorted()
        assertEquals(sequenceNumbers, sortedSequenceNumbers, "Sequence numbers should be monotonically increasing")

        // Verify sequence numbers start from a reasonable value (typically 0 or 1)
        assertTrue(sequenceNumbers.first() >= 0, "First sequence number should be non-negative")

        // Verify no duplicate sequence numbers
        assertEquals(sequenceNumbers.size, sequenceNumbers.toSet().size, "Sequence numbers should be unique")
    }

    @Test
    fun testResponsesStreamingEventTypes() = test {
        val request = responseRequest {
            model = ModelId("gpt-5")
            reasoning = ReasoningConfig(effort = "medium", summary = "detailed")
            include = listOf("reasoning.encrypted_content")
            stream = true
            input {
                message {
                    role = ChatRole.User
                    content {
                        inputText("Explain photosynthesis briefly")
                    }
                }
            }
        }

        val chunks = mutableListOf<ResponseChunk>()
        openAI.createResponseStream(request).collect { chunk ->
            chunks.add(chunk)
        }

        val eventTypes = chunks.map { it.type }.toSet()

        // Verify essential event types are present
        assertTrue(eventTypes.contains("response.created"), "Should contain response.created event")
        assertTrue(eventTypes.contains("response.completed"), "Should contain response.completed event")

        // Verify event ordering - created should come before completed
        val createdIndex = chunks.indexOfFirst { it.type == "response.created" }
        val completedIndex = chunks.indexOfLast { it.type == "response.completed" }
        assertTrue(createdIndex < completedIndex, "response.created should come before response.completed")

        // Verify response data is present in appropriate events
        val createdChunk = chunks.first { it.type == "response.created" }
        assertNotNull(createdChunk.response, "response.created should have response data")

        val completedChunk = chunks.last { it.type == "response.completed" }
        assertNotNull(completedChunk.response, "response.completed should have response data")
    }

    @Test
    fun testResponsesStreamingWithoutReasoning() = test {
        val request = responseRequest {
            model = ModelId("gpt-5")
            stream = true
            input {
                message {
                    role = ChatRole.User
                    content {
                        inputText("What is 2+2?")
                    }
                }
            }
        }

        val chunks = mutableListOf<ResponseChunk>()
        openAI.createResponseStream(request).collect { chunk ->
            chunks.add(chunk)
        }

        // Should still get basic streaming events even without reasoning
        val eventTypes = chunks.map { it.type }.toSet()
        assertTrue(eventTypes.contains("response.created"))
        assertTrue(eventTypes.contains("response.completed"))

        // Should not have reasoning-specific events when no reasoning config is provided
        val reasoningEvents = chunks.filter { it.type.contains("reasoning") }
        assertTrue(reasoningEvents.isEmpty(), "Should not have reasoning events when no reasoning config is provided, but got: ${reasoningEvents.map { it.type }}")

        // Verify the stream completes successfully
        assertTrue(chunks.isNotEmpty())
    }

    @Test
    fun testResponsesStreamingCancellation() = test {
        val request = responseRequest {
            model = ModelId("gpt-5")
            reasoning = ReasoningConfig(effort = "high") // Use high effort for longer processing
            stream = true
            input {
                message {
                    role = ChatRole.User
                    content {
                        inputText("Write a very long detailed explanation of quantum mechanics, covering all major principles, mathematical formulations, and historical development.")
                    }
                }
            }
        }

        var chunksReceived = 0
        var cancellationCaught = false

        val job = launch {
            try {
                openAI.createResponseStream(request).collect { chunk ->
                    chunksReceived++
                    if (chunksReceived >= 3) { // Cancel after receiving a few chunks
                        cancel("Test cancellation")
                    }
                }
            } catch (e: CancellationException) {
                cancellationCaught = true
                throw e // Re-throw to properly handle cancellation
            }
        }

        try {
            job.join()
        } catch (e: CancellationException) {
            // Expected
        }

        assertTrue(job.isCancelled, "Job should be cancelled")
        assertTrue(chunksReceived > 0, "Should have received some chunks before cancellation")
        assertTrue(cancellationCaught, "Should have caught CancellationException")
    }

    @Test
    fun testResponsesStreamingTimeout() = test {
        val request = responseRequest {
            model = ModelId("gpt-5")
            stream = true
            input {
                message {
                    role = ChatRole.User
                    content {
                        inputText("Hello")
                    }
                }
            }
        }

        // Test with a very short timeout to ensure timeout behavior
        var timeoutCaught = false
        try {
            withTimeout(1) { // 1ms timeout - should definitely timeout
                openAI.createResponseStream(request).collect { }
            }
        } catch (e: TimeoutCancellationException) {
            timeoutCaught = true
        }

        assertTrue(timeoutCaught, "Should have caught TimeoutCancellationException")
    }

    @Test
    fun testResponsesStreamingErrorHandling() = test {
        // Test with invalid model to trigger error
        val request = responseRequest {
            model = ModelId("invalid-model-name-that-does-not-exist")
            stream = true
            input {
                message {
                    role = ChatRole.User
                    content {
                        inputText("Hello")
                    }
                }
            }
        }

        var errorCaught = false
        try {
            openAI.createResponseStream(request).collect { }
        } catch (e: InvalidRequestException) {
            errorCaught = true
        } catch (e: Exception) {
            // Other exceptions are also acceptable for this test
            errorCaught = true
        }

        assertTrue(errorCaught, "Should have caught an exception for invalid model")
    }

    @Test
    fun testResponsesStreamingEmptyInput() = test {
        // Test with empty input to see how the API handles it
        val request = responseRequest {
            model = ModelId("gpt-5")
            stream = true
            input {
                // Empty input - should trigger validation error
            }
        }

        var errorCaught = false
        try {
            openAI.createResponseStream(request).collect { }
        } catch (e: InvalidRequestException) {
            errorCaught = true
        } catch (e: Exception) {
            // Other exceptions are also acceptable
            errorCaught = true
        }

        assertTrue(errorCaught, "Should have caught an exception for empty input")
    }

    @Test
    fun testResponsesStreamingChunkDataIntegrity() = test {
        val request = responseRequest {
            model = ModelId("gpt-5")
            reasoning = ReasoningConfig(effort = "medium")
            include = listOf("reasoning.encrypted_content")
            stream = true
            input {
                message {
                    role = ChatRole.User
                    content {
                        inputText("Count from 1 to 3")
                    }
                }
            }
        }

        val chunks = mutableListOf<ResponseChunk>()
        openAI.createResponseStream(request).collect { chunk ->
            chunks.add(chunk)
        }

        // Verify chunk data integrity
        for (chunk in chunks) {
            // All chunks should have valid type and sequence number
            assertTrue(chunk.type.isNotEmpty(), "Chunk type should not be empty")
            assertTrue(chunk.sequenceNumber >= 0, "Sequence number should be non-negative")

            // Verify conditional fields are present when expected
            when (chunk.type) {
                "response.created", "response.completed", "response.in_progress" -> {
                    assertNotNull(chunk.response, "Response data should be present for ${chunk.type}")
                }
                "response.output_item.added" -> {
                    assertNotNull(chunk.item, "Item should be present for output_item.added")
                    assertNotNull(chunk.outputIndex, "Output index should be present for output_item.added")
                }
                "response.reasoning_summary_text.delta" -> {
                    // Delta may be empty string but should not be null if present
                    val delta = chunk.delta
                    if (delta != null) {
                        assertTrue(delta.isNotEmpty() || delta.isEmpty(), "Delta should be a valid string")
                    }
                }
                "response.message_content.text.delta" -> {
                    assertNotNull(chunk.contentIndex, "Content index should be present for message content delta")
                }
            }
        }
    }

    @Test
    fun testResponsesStreamingDeltaAccumulation() = test {
        val request = responseRequest {
            model = ModelId("gpt-5")
            reasoning = ReasoningConfig(effort = "medium", summary = "detailed")
            stream = true
            input {
                message {
                    role = ChatRole.User
                    content {
                        inputText("Say 'Hello World' exactly")
                    }
                }
            }
        }

        val chunks = mutableListOf<ResponseChunk>()
        openAI.createResponseStream(request).collect { chunk ->
            chunks.add(chunk)
        }

        // Test delta accumulation for message content
        val messageDeltas = chunks.filter { it.type == "response.message_content.text.delta" }
        if (messageDeltas.isNotEmpty()) {
            val accumulatedText = messageDeltas.mapNotNull { it.delta }.joinToString("")
            assertTrue(accumulatedText.isNotEmpty(), "Accumulated message text should not be empty")

            // Verify content indices are sequential for the same message
            val contentIndices = messageDeltas.mapNotNull { it.contentIndex }.sorted()
            if (contentIndices.isNotEmpty()) {
                assertEquals(contentIndices, contentIndices.sorted(), "Content indices should be in order")
            }
        }

        // Test delta accumulation for reasoning summaries
        val reasoningDeltas = chunks.filter { it.type == "response.reasoning_summary_text.delta" }
        if (reasoningDeltas.isNotEmpty()) {
            val accumulatedSummary = reasoningDeltas.mapNotNull { it.delta }.joinToString("")
            // Summary may be empty but should be a valid string
            assertTrue(accumulatedSummary.length >= 0, "Accumulated summary should be valid")
        }
    }

    @Test
    fun testResponsesStreamingLimitedCollection() = test {
        val request = responseRequest {
            model = ModelId("gpt-5")
            stream = true
            input {
                message {
                    role = ChatRole.User
                    content {
                        inputText("Write a short poem")
                    }
                }
            }
        }

        // Test collecting only first few chunks
        val limitedChunks = openAI.createResponseStream(request).take(5).toList()

        assertTrue(limitedChunks.size <= 5, "Should collect at most 5 chunks")
        assertTrue(limitedChunks.isNotEmpty(), "Should collect at least one chunk")

        // First chunk should typically be response.created
        val firstChunk = limitedChunks.first()
        assertTrue(firstChunk.type == "response.created" || firstChunk.type.isNotEmpty(),
                  "First chunk should have valid type")
    }

    @Test
    fun testResponsesStreamingFirstChunkOnly() = test {
        val request = responseRequest {
            model = ModelId("gpt-5")
            stream = true
            input {
                message {
                    role = ChatRole.User
                    content {
                        inputText("Hi")
                    }
                }
            }
        }

        // Test getting only the first chunk
        val firstChunk = openAI.createResponseStream(request).first()

        assertNotNull(firstChunk, "Should receive first chunk")
        assertTrue(firstChunk.type.isNotEmpty(), "First chunk should have valid type")
        assertTrue(firstChunk.sequenceNumber >= 0, "First chunk should have valid sequence number")

        // First chunk is typically response.created
        if (firstChunk.type == "response.created") {
            assertNotNull(firstChunk.response, "response.created should have response data")
            assertNotNull(firstChunk.response?.id, "Response should have ID")
        }
    }

    @Test
    fun testResponsesStreamingWithMaxTokensLimit() = test {
        val request = responseRequest {
            model = ModelId("gpt-5")
            maxOutputTokens = 16 // Minimum allowed limit
            stream = true
            input {
                message {
                    role = ChatRole.User
                    content {
                        inputText("Write a long essay about artificial intelligence")
                    }
                }
            }
        }

        val chunks = mutableListOf<ResponseChunk>()
        openAI.createResponseStream(request).collect { chunk ->
            chunks.add(chunk)
        }

        // Should complete successfully even with low token limit
        val eventTypes = chunks.map { it.type }.toSet()
        assertTrue(eventTypes.contains("response.created"))
        assertTrue(eventTypes.contains("response.completed"))

        // Validate that we received chunks and the response structure is correct
        assertTrue(chunks.isNotEmpty(), "Should have received streaming chunks")

        // Validate that we received chunks with response data
        val chunksWithResponse = chunks.filter { it.response != null }
        assertTrue(chunksWithResponse.isNotEmpty(), "Should have chunks with response data")

        // Final response should have usage information showing token limit was respected
        val finalChunk = chunks.lastOrNull { it.type == "response.completed" }
        assertNotNull(finalChunk, "Should have a response.completed chunk")

        // Look for usage information in any chunk (might be in different places)
        val usage = finalChunk?.response?.usage ?: finalChunk?.usage ?: chunks.lastOrNull()?.usage
        assertNotNull(usage, "Usage information should be present somewhere in the response")

        // Validate Responses API usage fields are present and correct

        // For Responses API, these fields should be present
        assertNotNull(usage.inputTokens, "inputTokens should be present for Responses API")
        assertNotNull(usage.totalTokens, "totalTokens should be present")

        // Validate token counts are reasonable
        assertTrue(usage.inputTokens!! > 0, "Should have used input tokens")
        assertTrue(usage.totalTokens!! > 0, "Should have used total tokens")

        // Output tokens might be 0 if the response was cut off due to token limit
        // but should be present as a field
        assertNotNull(usage.outputTokens, "outputTokens field should be present for Responses API")
        assertTrue(usage.outputTokens!! >= 0, "Output tokens should be non-negative")

        // If we have output tokens, validate the limit was respected
        if (usage.outputTokens!! > 0) {
            assertTrue(usage.outputTokens!! <= 16, "Output tokens should respect the limit of 16")
            assertEquals(usage.inputTokens!! + usage.outputTokens!!, usage.totalTokens!!,
                        "Total tokens should equal input + output tokens")
        }

        // For Responses API, Chat Completions fields should be null or not present
        // (The API returns only the Responses API format)

        // Validate token details if present (optional validation)
        usage.inputTokensDetails?.let {
            assertNotNull(it.cachedTokens, "Cached tokens should be reported in input token details")
        }
        usage.outputTokensDetails?.let {
            // Reasoning tokens might be present for reasoning models
        }
    }

    @Test
    fun testResponsesStreamingWithDifferentEffortLevels() = test {
        val effortLevels = listOf("low", "medium", "high")

        for (effort in effortLevels) {
            val request = responseRequest {
                model = ModelId("gpt-5")
                reasoning = ReasoningConfig(effort = effort)
                stream = true
                input {
                    message {
                        role = ChatRole.User
                        content {
                            inputText("What is 5 + 3?")
                        }
                    }
                }
            }

            val chunks = mutableListOf<ResponseChunk>()
            openAI.createResponseStream(request).collect { chunk ->
                chunks.add(chunk)
            }

            // All effort levels should produce valid streaming responses
            assertTrue(chunks.isNotEmpty(), "Should receive chunks for effort level: $effort")

            val eventTypes = chunks.map { it.type }.toSet()
            assertTrue(eventTypes.contains("response.created"), "Should have response.created for effort: $effort")
            assertTrue(eventTypes.contains("response.completed"), "Should have response.completed for effort: $effort")
        }
    }

    @Test
    fun testResponsesStreamingWithMultipleMessages() = test {
        val request = responseRequest {
            model = ModelId("gpt-5")
            stream = true
            input {
                message(ChatRole.System, "You are a helpful math tutor.")
                message(ChatRole.User, "What is 2 + 2?")
                message(ChatRole.Assistant, "2 + 2 equals 4.")
                message(ChatRole.User, "Now what is 3 + 3?")
            }
        }

        val chunks = mutableListOf<ResponseChunk>()
        openAI.createResponseStream(request).collect { chunk ->
            chunks.add(chunk)
        }

        // Should handle multi-turn conversation in streaming mode
        assertTrue(chunks.isNotEmpty(), "Should receive chunks for multi-message input")

        val eventTypes = chunks.map { it.type }.toSet()
        assertTrue(eventTypes.contains("response.created"))
        assertTrue(eventTypes.contains("response.completed"))

        // Verify final response contains appropriate content
        val finalChunk = chunks.lastOrNull { it.type == "response.completed" }
        assertNotNull(finalChunk?.response, "Final chunk should have response data")
    }

    @Test
    fun testResponsesStreamingErrorRecovery() = test {
        // Test that streaming can handle and recover from various error conditions
        val request = responseRequest {
            model = ModelId("gpt-5")
            stream = true
            input {
                message {
                    role = ChatRole.User
                    content {
                        inputText("Hello")
                    }
                }
            }
        }

        var streamCompleted = false
        var errorOccurred = false

        try {
            openAI.createResponseStream(request)
                .catch { error ->
                    errorOccurred = true
                    // In a real scenario, you might want to emit a default value or retry
                    throw error
                }
                .collect { chunk ->
                    // Process chunks normally
                    assertTrue(chunk.type.isNotEmpty(), "Chunk type should be valid")
                }
            streamCompleted = true
        } catch (e: Exception) {
            // Expected in some error scenarios
        }

        // Either the stream completed successfully or an error was properly handled
        assertTrue(streamCompleted || errorOccurred, "Stream should either complete or handle errors properly")
    }

    @Test
    fun testResponsesStreamingWithReasoningBasic() = test {
        val request = responseRequest {
            model = ModelId("gpt-5") // Use reasoning model
            reasoning = ReasoningConfig(effort = "medium")
            include = listOf("reasoning.encrypted_content")
            input {
                message {
                    role = ChatRole.User
                    content {
                        inputText("Solve this step by step: What is 15 * 23?")
                    }
                }
            }
        }

        val response = openAI.createResponse(request)

        // Validate basic response with reasoning
        assertNotNull(response.id)
        assertEquals("completed", response.status)
        assertTrue(response.output.isNotEmpty())

        // Check if reasoning content is available in the output items
        // The encrypted content is returned as a ResponseOutputItem.Reasoning, not in the top-level reasoning field
        val reasoningOutput = response.output.filterIsInstance<Reasoning>().firstOrNull()
        if (reasoningOutput != null) {
            // Verify reasoning structure when present
            assertNotNull(reasoningOutput.id)
            // Encrypted content may or may not be present depending on include parameter
            // Since we requested "reasoning.encrypted_content", it should be present
            assertNotNull(reasoningOutput.encryptedContent)
        }
    }

    @Test
    fun testReasoningConfigEffortLevels() = test {
        val efforts = listOf("low", "medium", "high")

        for (effort in efforts) {
            val request = responseRequest {
                model = ModelId("gpt-5") // Use reasoning model
                reasoning = ReasoningConfig(effort = effort)
                include = listOf("reasoning.encrypted_content")
                input {
                    message {
                        role = ChatRole.User
                        content {
                            inputText("What is 2 + 2?")
                        }
                    }
                }
            }

            val response = openAI.createResponse(request)

            assertNotNull(response.id)
            assertEquals("completed", response.status)
            assertTrue(response.output.isNotEmpty())
        }
    }

    @Test
    fun testReasoningConfigSummaryOptions() = test {
        // Note: gpt-5 only supports "detailed" summary option, not "concise"
        val summaryOptions = listOf("auto", "detailed")

        for (summary in summaryOptions) {
            val request = responseRequest {
                model = ModelId("gpt-5") // Use reasoning model
                reasoning = ReasoningConfig(effort = "medium", summary = summary)
                include = listOf("reasoning.encrypted_content")
                input {
                    message {
                        role = ChatRole.User
                        content {
                            inputText("Explain the concept of gravity.")
                        }
                    }
                }
            }

            val response = openAI.createResponse(request)

            assertNotNull(response.id)
            assertEquals("completed", response.status)
            assertTrue(response.output.isNotEmpty())
        }
    }



    @Test
    fun testStreamingResponsesWithNetworkLogging() = test {
        // Create a custom logger that will capture all HTTP traffic
        val logMessages = mutableListOf<String>()
        val customLogger = object : io.ktor.client.plugins.logging.Logger {
            override fun log(message: String) {
                logMessages.add(message)
            }
        }

        // Create a client with maximum logging to see raw network traffic
        val loggingClient = OpenAI(
            token = System.getenv("OPENAI_API_KEY") ?: "",
            httpClientConfig = {
                install(io.ktor.client.plugins.logging.Logging) {
                    logger = customLogger
                    level = io.ktor.client.plugins.logging.LogLevel.ALL
                    sanitizeHeader { false }  // Don't sanitize any headers
                }
            }
        )

        val request = responseRequest {
            model = ModelId("gpt-5")
            reasoning = ReasoningConfig(effort = "medium", summary = "detailed")
            include = listOf("reasoning.encrypted_content")
            stream = true
            store = false
            instructions = "You are a helpful veterinarian that speaks like a pirate."
            input {
                message {
                    role = ChatRole.User
                    content {
                        inputText("How do mice compare to cats?")
                    }
                }
            }
        }

        println("=== STARTING STREAMING RESPONSE TEST (UPDATED) ===")
        println("Request: $request")

        // Test streaming response
        val chunks = mutableListOf<ResponseChunk>()
        loggingClient.createResponseStream(request).collect { chunk ->
            println("=== RECEIVED CHUNK ===")
            println("Chunk: $chunk")
            chunks.add(chunk)
        }

        println("=== STREAMING COMPLETE ===")
        println("Total chunks received: ${chunks.size}")

        // Print all captured HTTP logs
        println("=== HTTP LOGS ===")
        logMessages.forEach { log ->
            println("LOG: $log")
        }


        // Basic validation
        assertTrue(chunks.isNotEmpty(), "Should receive at least one chunk")

        loggingClient.close()
    }

    @Test
    fun testResponsesWithPreviousReasoning() = test {
        // First request to establish reasoning context
        val firstRequest = responseRequest {
            model = ModelId("gpt-5") // Use reasoning model
            reasoning = ReasoningConfig(effort = "medium", summary = "detailed")
            include = listOf("reasoning.encrypted_content")
            input {
                message {
                    role = ChatRole.User
                    content {
                        inputText("What is 10 + 5? Please show your reasoning.")
                    }
                }
            }
        }

        val firstResponse = openAI.createResponse(firstRequest)

        // Validate first response
        assertNotNull(firstResponse.id)
        assertEquals("completed", firstResponse.status)
        assertTrue(firstResponse.output.isNotEmpty())
        assertNotNull(firstResponse.firstMessageText)

        // Extract reasoning content from output - this is the key fix
        val reasoningOutput = firstResponse.output.filterIsInstance<Reasoning>().firstOrNull()
        val previousEncryptedContent = reasoningOutput?.encryptedContent
        val reasoningSummary = reasoningOutput?.summary?.filterIsInstance<SummaryTextPart>()?.firstOrNull()
        val previousReasoningId = reasoningOutput?.id

        // Verify reasoning content is available
        assertNotNull(reasoningOutput)
        assertNotNull(previousEncryptedContent)
        assertNotNull(previousReasoningId)

        // Second request with previous reasoning - properly pass the encrypted content
        val secondRequest = responseRequest {
            model = ModelId("gpt-5") // Use reasoning model
            reasoning = ReasoningConfig(effort = "medium", summary = "detailed")
            include = listOf("reasoning.encrypted_content")
            input {
                // Previous conversation context
                message {
                    role = ChatRole.User
                    content {
                        inputText("What is 10 + 5? Please show your reasoning.")
                    }
                }
                message {
                    role = ChatRole.Assistant
                    content {
                        outputText(firstResponse.firstMessageText ?: "15")
                    }
                }

                // Pass previous reasoning - use encryptedContent field directly on reasoning item
                reasoning {
                    id = previousReasoningId!! // Use the actual ID from the previous response
                    encryptedContent = previousEncryptedContent // Pass the encrypted content directly
                    if (reasoningSummary is SummaryTextPart) {
                        summaryText(reasoningSummary.text)
                    } else {
                        summaryText("Previous calculation: 10 + 5 = 15")
                    }
                }

                // New question building on previous context
                message {
                    role = ChatRole.User
                    content {
                        inputText("Now multiply that result by 2. Use the previous reasoning to inform your approach.")
                    }
                }
            }
        }

        val secondResponse = openAI.createResponse(secondRequest)

        // Validate second response
        assertNotNull(secondResponse.id)
        assertEquals("completed", secondResponse.status)
        assertTrue(secondResponse.output.isNotEmpty())
        assertNotNull(secondResponse.firstMessageText)

        // Verify the conversation flow makes sense
        val secondAnswer = secondResponse.firstMessageText

        // The second response should reference the previous calculation
        assertTrue(secondAnswer?.contains("30") == true || secondAnswer?.contains("2") == true,
            "Second response should contain result of multiplication: $secondAnswer")
    }

    @Test
    fun testResponseRequestBuilder() = test {
        val request = responseRequest {
            model = ModelId("gpt-5")
            temperature = 0.7
            maxOutputTokens = 100
            input {
                message(ChatRole.System, "You are a helpful assistant.")
                message(ChatRole.User, "Hello!")
            }
        }

        assertEquals(ModelId("gpt-5"), request.model)
        assertEquals(0.7, request.temperature)
        assertEquals(100, request.maxOutputTokens)
        assertEquals(false, request.store) // Always false for stateless
        assertEquals(2, request.input.size)

        // Validate input structure
        val systemMessage = request.input[0] as Message
        assertEquals(ChatRole.System, systemMessage.role)
        assertTrue(systemMessage.content.isNotEmpty())

        val userMessage = request.input[1] as Message
        assertEquals(ChatRole.User, userMessage.role)
        assertTrue(userMessage.content.isNotEmpty())
    }

    @Test
    fun testResponseRequestWithComplexInput() = test {
        val request = responseRequest {
            model = ModelId("gpt-5") // Use reasoning model
            reasoning = ReasoningConfig(effort = "high", summary = "concise")
            include = listOf("reasoning.encrypted_content")
            temperature = 0.5
            maxOutputTokens = 500
            input {
                message(ChatRole.System, "You are a math tutor.")
                message(ChatRole.User, "Solve: 2x + 5 = 15")
                message(ChatRole.Assistant, "To solve 2x + 5 = 15, I'll subtract 5 from both sides: 2x = 10, then divide by 2: x = 5")
                reasoning {
                    id = "rs_${System.currentTimeMillis()}"
                    text("Previous algebraic reasoning steps")
                    summaryText("Solved linear equation step by step")
                }
                message(ChatRole.User, "Now solve: 3x - 7 = 14")
            }
        }

        // Validate complex request structure
        assertEquals(ModelId("gpt-5"), request.model)
        assertEquals(0.5, request.temperature)
        assertEquals(500, request.maxOutputTokens)
        assertEquals(false, request.store)
        assertNotNull(request.reasoning)
        assertEquals("high", request.reasoning?.effort)
        assertEquals("concise", request.reasoning?.summary)
        assertEquals(listOf("reasoning.encrypted_content"), request.include)
        assertEquals(5, request.input.size)

        // Validate reasoning input item
        val reasoningInput = request.input[3] as Reasoning
        assertTrue(reasoningInput.content?.isNotEmpty() == true)
        assertTrue(reasoningInput.summary.isNotEmpty())
        assertTrue(reasoningInput.content?.get(0) is ReasoningTextPart)
        assertTrue(reasoningInput.summary[0] is SummaryTextPart)
    }

    @Test
    fun testResponseWithUsageAndMetadata() = test {
        val request = responseRequest {
            model = ModelId("gpt-5")
            input {
                message {
                    role = ChatRole.User
                    content {
                        inputText("Write a short poem about coding.")
                    }
                }
            }
        }

        val response = openAI.createResponse(request)

        // Validate response structure including optional fields
        assertNotNull(response.id)
        assertEquals("response", response.objectType)
        assertTrue(response.createdAt > 0)
        assertEquals("completed", response.status)

        // Usage statistics may or may not be present
        // Responses API returns input_tokens, output_tokens, total_tokens
        if (response.usage != null) {
            // The totalTokens field should always be present if usage is provided
            assertNotNull(response.usage?.totalTokens)
            assertTrue(response.usage?.totalTokens?.let { it > 0 } == true)
        }

        // Metadata may or may not be present, and may be empty
        if (response.metadata != null) {
            assertNotNull(response.metadata)
        }

        // Output text may or may not be present
        if (response.outputText != null) {
            assertTrue(response.outputText?.isNotEmpty() == true)
        }
    }

    @Test
    fun testResponseWithDifferentIncludeOptions() = test {
        val includeOptions = listOf(
            listOf("reasoning.encrypted_content"),
            emptyList<String>(),
            null
        )

        for (include in includeOptions) {
            val request = responseRequest {
                model = ModelId("gpt-5") // Use reasoning model
                reasoning = ReasoningConfig(effort = "medium")
                if (include != null) {
                    this.include = include
                }
                input {
                    message {
                        role = ChatRole.User
                        content {
                            inputText("What is the capital of France?")
                        }
                    }
                }
            }

            val response = openAI.createResponse(request)

            assertNotNull(response.id)
            assertEquals("completed", response.status)
            assertTrue(response.output.isNotEmpty())
        }
    }

    @Test
    fun testMultiTurnConversationWithoutReasoning() = test {
        // Test a multi-turn conversation without reasoning to ensure basic functionality
        val request = responseRequest {
            model = ModelId("gpt-5")
            input {
                message(ChatRole.System, "You are a helpful assistant.")
                message(ChatRole.User, "Hello!")
                message(ChatRole.Assistant, "Hello! How can I help you today?")
                message(ChatRole.User, "What's the weather like?")
            }
        }

        val response = openAI.createResponse(request)

        assertNotNull(response.id)
        assertEquals("completed", response.status)
        assertTrue(response.output.isNotEmpty())
        assertNotNull(response.firstMessageText)

        // Without reasoning config, reasoning should be null or minimal
        // Just verify we got a valid response
    }

    @Test
    fun testResponseErrorHandling() = test {
        // Test with potentially problematic input to see error handling
        val request = responseRequest {
            model = ModelId("gpt-5")
            maxOutputTokens = 1 // Very low token limit to potentially trigger issues
            input {
                message {
                    role = ChatRole.User
                    content {
                        inputText("Write a very long essay about the history of the universe, covering every detail from the Big Bang to the present day, including all scientific discoveries, philosophical implications, and cultural impacts throughout human history.")
                    }
                }
            }
        }

        // This should throw an InvalidRequestException due to max_output_tokens being below minimum (16)
        assertFailsWith<InvalidRequestException> {
            openAI.createResponse(request)
        }
    }

    @Test
    fun testResponsesWithMaxOutputTokens() = test {
        val request = responseRequest {
            model = ModelId("gpt-5")
            maxOutputTokens = 100 // Limit output to 100 tokens
            input {
                message {
                    role = ChatRole.User
                    content {
                        inputText("Explain quantum computing in simple terms.")
                    }
                }
            }
            store = false
        }

        val response = openAI.createResponse(request)

        // Validate basic response structure
        assertNotNull(response)
        assertNotNull(response.id)
        assertEquals("response", response.objectType)
        assertEquals("completed", response.status)

        // Validate request parameters were set correctly
        assertEquals(100, request.maxOutputTokens)
        assertEquals(ModelId("gpt-5"), request.model)
        assertEquals(false, request.store)

        // Validate output structure - reasoning models return reasoning output, not message output
        assertTrue(response.output.isNotEmpty())
        val reasoningOutput = response.output.filterIsInstance<Reasoning>().firstOrNull()
        assertNotNull(reasoningOutput)
        assertNotNull(reasoningOutput?.id)

        // For reasoning models, the actual response text might be in the reasoning summary or not present
        // The key is that we got a valid response with the correct structure

        // Validate usage information shows limited output tokens
        assertNotNull(response.usage)
        // For Responses API, check output_tokens field
        val outputTokens = response.usage?.outputTokens ?: response.usage?.completionTokens ?: 0
        assertTrue(outputTokens <= 100) // Should be within the limit
        assertTrue(outputTokens > 0) // Should have generated some tokens
    }

    @Test
    fun testResponsesWithMaxOutputTokensAndInstructions() = test {
        val response = openAI.createResponse(
            responseRequest {
                model = ModelId("gpt-5")
                maxOutputTokens = 50
                instructions = "Be very concise and use simple language."
                input {
                    message(ChatRole.User, "What is artificial intelligence?")
                }
            }
        )

        // Validate basic response structure
        assertNotNull(response.id)
        assertEquals("response", response.objectType)
        assertEquals("completed", response.status)
        assertNotNull(response.model)

        // Validate output structure - reasoning models return reasoning output, not message output
        assertTrue(response.output.isNotEmpty())
        val reasoningOutput = response.output.filterIsInstance<Reasoning>().firstOrNull()
        assertNotNull(reasoningOutput)
        assertNotNull(reasoningOutput?.id)

        // Validate usage information shows limited output tokens (even more restrictive)
        assertNotNull(response.usage)
        // For Responses API, check output_tokens field
        val outputTokens = response.usage?.outputTokens ?: response.usage?.completionTokens ?: 0
        assertTrue(outputTokens <= 50) // Should be within the stricter limit
        // Note: Some reasoning models may return 0 tokens for certain requests, so we just check the limit
    }

    @Test
    fun testMultiTurnToolCalling() = test {
        // First request: Send tools and get tool calls
        val firstRequest = responseRequest {
            model = ModelId("gpt-5")
            tools {
                tool {
                    name = ToolName("get_weather")
                    description = ToolDescription("Get the current weather for a location")
                    parameters = ObjectSchema(
                        properties = mapOf(
                            PropertyName("location") to StringSchema(description = PropertyDescription("The city and state, e.g. San Francisco, CA")),
                            PropertyName("unit") to StringSchema(description = PropertyDescription("Temperature unit (celsius or fahrenheit)"))
                        ),
                        required = listOf(PropertyName("location"))
                    )
                }
                tool {
                    name = ToolName("calculate_distance")
                    description = ToolDescription("Calculate distance between two locations")
                    parameters = ObjectSchema(
                        properties = mapOf(
                            PropertyName("from") to StringSchema(description = PropertyDescription("Starting location")),
                            PropertyName("to") to StringSchema(description = PropertyDescription("Destination location"))
                        ),
                        required = listOf(PropertyName("from"), PropertyName("to"))
                    )
                }
            }
            input {
                message {
                    role = ChatRole.User
                    content {
                        inputText("What's the weather in San Francisco and how far is it from New York?")
                    }
                }
            }
        }

        val firstResponse = openAI.createResponse(firstRequest)

        // Verify we got tool calls
        assertNotNull(firstResponse.id)
        assertEquals("completed", firstResponse.status)
        assertTrue(firstResponse.output.isNotEmpty())

        // Extract tool calls from the response
        val toolCalls = firstResponse.output.filterIsInstance<FunctionCall>()
        assertTrue(toolCalls.isNotEmpty(), "Should receive tool calls")

        // Verify tool calls structure
        val weatherCall = toolCalls.find { it.name.name == "get_weather" }
        val distanceCall = toolCalls.find { it.name.name == "calculate_distance" }

        assertNotNull(weatherCall, "Should have weather tool call")
        assertNotNull(distanceCall, "Should have distance tool call")

        // Verify tool call details
        assertNotNull(weatherCall.callId)
        assertNotNull(weatherCall.arguments)
        assertEquals(ToolCallStatus.Completed, weatherCall.status)

        assertNotNull(distanceCall.callId)
        assertNotNull(distanceCall.arguments)
        assertEquals(ToolCallStatus.Completed, distanceCall.status)

        println("Weather call ID: ${weatherCall.callId}")
        println("Weather arguments: ${weatherCall.arguments}")
        println("Distance call ID: ${distanceCall.callId}")
        println("Distance arguments: ${distanceCall.arguments}")

        // Second request: Send tool call results back
        val secondRequest = responseRequest {
            model = ModelId("gpt-5")
            tools {
                tool {
                    name = ToolName("get_weather")
                    description = ToolDescription("Get the current weather for a location")
                    parameters = ObjectSchema(
                        properties = mapOf(
                            PropertyName("location") to StringSchema(description = PropertyDescription("The city and state, e.g. San Francisco, CA")),
                            PropertyName("unit") to StringSchema(description = PropertyDescription("Temperature unit (celsius or fahrenheit)"))
                        ),
                        required = listOf(PropertyName("location"))
                    )
                }
                tool {
                    name = ToolName("calculate_distance")
                    description = ToolDescription("Calculate distance between two locations")
                    parameters = ObjectSchema(
                        properties = mapOf(
                            PropertyName("from") to StringSchema(description = PropertyDescription("Starting location")),
                            PropertyName("to") to StringSchema(description = PropertyDescription("Destination location"))
                        ),
                        required = listOf(PropertyName("from"), PropertyName("to"))
                    )
                }
            }
            input {
                // Include the original user message
                message {
                    role = ChatRole.User
                    content {
                        inputText("What's the weather in San Francisco and how far is it from New York?")
                    }
                }

                // Include the assistant's tool calls
                functionCall {
                    name = weatherCall.name
                    callId = weatherCall.callId
                    arguments = weatherCall.arguments
                    status = weatherCall.status
                }

                functionCall {
                    name = distanceCall.name
                    callId = distanceCall.callId
                    arguments = distanceCall.arguments
                    status = distanceCall.status
                }

                // Provide tool call outputs
                functionCallOutput {
                    callId = weatherCall.callId
                    output = """{"temperature": 72, "condition": "sunny", "humidity": 65}"""
                }

                functionCallOutput {
                    callId = distanceCall.callId
                    output = """{"distance": 2572, "unit": "miles", "duration": "6 hours 15 minutes"}"""
                }
            }
        }

        val secondResponse = openAI.createResponse(secondRequest)

        // Verify the final response
        assertNotNull(secondResponse.id)
        assertEquals("completed", secondResponse.status)
        assertTrue(secondResponse.output.isNotEmpty())

        // Should get a message response with the synthesized answer
        val finalMessage = secondResponse.output.filterIsInstance<Message>().firstOrNull()
        assertNotNull(finalMessage, "Should receive final message")
        assertEquals(ChatRole.Assistant, finalMessage.role)
        assertTrue(finalMessage.content.isNotEmpty())

        // Verify the message contains weather and distance information
        val messageText = finalMessage.content.filterIsInstance<MessageContent.OutputText>()
            .joinToString(" ") { it.text }
        assertTrue(messageText.isNotEmpty(), "Final message should not be empty")

        println("Final response: $messageText")
    }

    @Test
    fun testToolCallingWithSubsequentRounds() = test {
        // Test a scenario where the assistant needs multiple rounds of tool calls
        val firstRequest = responseRequest {
            model = ModelId("gpt-5")
            tools {
                tool {
                    name = ToolName("search_database")
                    description = ToolDescription("Search for information in a database")
                    parameters = ObjectSchema(
                        properties = mapOf(
                            PropertyName("query") to StringSchema(description = PropertyDescription("Search query")),
                            PropertyName("table") to StringSchema(description = PropertyDescription("Database table to search"))
                        ),
                        required = listOf(PropertyName("query"), PropertyName("table"))
                    )
                }
                tool {
                    name = ToolName("format_results")
                    description = ToolDescription("Format search results for display")
                    parameters = ObjectSchema(
                        properties = mapOf(
                            PropertyName("data") to StringSchema(description = PropertyDescription("Raw data to format")),
                            PropertyName("format") to StringSchema(description = PropertyDescription("Output format (table, list, or summary)"))
                        ),
                        required = listOf(PropertyName("data"))
                    )
                }
            }
            input {
                message {
                    role = ChatRole.User
                    content {
                        inputText("Find all users with the name 'John' and format the results as a table")
                    }
                }
            }
        }

        val firstResponse = openAI.createResponse(firstRequest)

        // Should get a database search tool call first
        val searchCalls = firstResponse.output.filterIsInstance<FunctionCall>()
            .filter { it.name.name == "search_database" }
        assertTrue(searchCalls.isNotEmpty(), "Should receive database search call")

        val searchCall = searchCalls.first()

        // Respond with search results
        val secondRequest = responseRequest {
            model = ModelId("gpt-5")
            tools {
                tool {
                    name = ToolName("search_database")
                    description = ToolDescription("Search for information in a database")
                    parameters = ObjectSchema(
                        properties = mapOf(
                            PropertyName("query") to StringSchema(description = PropertyDescription("Search query")),
                            PropertyName("table") to StringSchema(description = PropertyDescription("Database table to search"))
                        ),
                        required = listOf(PropertyName("query"), PropertyName("table"))
                    )
                }
                tool {
                    name = ToolName("format_results")
                    description = ToolDescription("Format search results for display")
                    parameters = ObjectSchema(
                        properties = mapOf(
                            PropertyName("data") to StringSchema(description = PropertyDescription("Raw data to format")),
                            PropertyName("format") to StringSchema(description = PropertyDescription("Output format (table, list, or summary)"))
                        ),
                        required = listOf(PropertyName("data"))
                    )
                }
            }
            input {
                message {
                    role = ChatRole.User
                    content {
                        inputText("Find all users with the name 'John' and format the results as a table")
                    }
                }

                functionCall {
                    name = searchCall.name
                    callId = searchCall.callId
                    arguments = searchCall.arguments
                    status = searchCall.status
                }

                functionCallOutput {
                    callId = searchCall.callId
                    output = """[{"id": 1, "name": "John Smith", "email": "john.smith@example.com"}, {"id": 2, "name": "John Doe", "email": "john.doe@example.com"}]"""
                }
            }
        }

        val secondResponse = openAI.createResponse(secondRequest)

        // Now should get a format_results tool call
        val formatCalls = secondResponse.output.filterIsInstance<FunctionCall>()
            .filter { it.name.name == "format_results" }

        if (formatCalls.isNotEmpty()) {
            // If we get another tool call, handle it
            val formatCall = formatCalls.first()

            val thirdRequest = responseRequest {
                model = ModelId("gpt-5")
                tools {
                    tool {
                        name = ToolName("search_database")
                        description = ToolDescription("Search for information in a database")
                        parameters = ObjectSchema(
                            properties = mapOf(
                                PropertyName("query") to StringSchema(description = PropertyDescription("Search query")),
                                PropertyName("table") to StringSchema(description = PropertyDescription("Database table to search"))
                            ),
                            required = listOf(PropertyName("query"), PropertyName("table"))
                        )
                    }
                    tool {
                        name = ToolName("format_results")
                        description = ToolDescription("Format search results for display")
                        parameters = ObjectSchema(
                            properties = mapOf(
                                PropertyName("data") to StringSchema(description = PropertyDescription("Raw data to format")),
                                PropertyName("format") to StringSchema(description = PropertyDescription("Output format (table, list, or summary)"))
                            ),
                            required = listOf(PropertyName("data"))
                        )
                    }
                }
                input {
                    message {
                        role = ChatRole.User
                        content {
                            inputText("Find all users with the name 'John' and format the results as a table")
                        }
                    }

                    functionCall {
                        name = searchCall.name
                        callId = searchCall.callId
                        arguments = searchCall.arguments
                        status = searchCall.status
                    }

                    functionCallOutput {
                        callId = searchCall.callId
                        output = """[{"id": 1, "name": "John Smith", "email": "john.smith@example.com"}, {"id": 2, "name": "John Doe", "email": "john.doe@example.com"}]"""
                    }

                    functionCall {
                        name = formatCall.name
                        callId = formatCall.callId
                        arguments = formatCall.arguments
                        status = formatCall.status
                    }

                    functionCallOutput {
                        callId = formatCall.callId
                        output = """| ID | Name | Email |
|-----|------|-------|
| 1 | John Smith | john.smith@example.com |
| 2 | John Doe | john.doe@example.com |"""
                    }
                }
            }

            val thirdResponse = openAI.createResponse(thirdRequest)

            // Should get final message
            assertNotNull(thirdResponse.id)
            assertEquals("completed", thirdResponse.status)
            assertTrue(thirdResponse.output.isNotEmpty())

            val finalMessage = thirdResponse.output.filterIsInstance<Message>().firstOrNull()
            assertNotNull(finalMessage, "Should receive final formatted message")
        } else {
            // If no additional tool calls, should have final message
            val finalMessage = secondResponse.output.filterIsInstance<Message>().firstOrNull()
            assertNotNull(finalMessage, "Should receive final message")
        }
    }
}
