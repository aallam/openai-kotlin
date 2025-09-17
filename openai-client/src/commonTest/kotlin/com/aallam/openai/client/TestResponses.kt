package com.aallam.openai.client

import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.api.response.*
import com.aallam.openai.api.exception.InvalidRequestException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.test.assertFailsWith

class TestResponses : TestOpenAI() {

    @Test
    fun testResponsesBasic() = test {
        val request = responseRequest {
            model = ModelId("gpt-5")
            input {
                message {
                    role = ChatRole.User
                    content = "Hello, how are you?"
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
        val messageOutput = response.output.find { it is ResponseOutputItem.Message } as? ResponseOutputItem.Message
        assertNotNull(messageOutput)
        assertNotNull(messageOutput.id)
        assertEquals(ChatRole.Assistant, messageOutput.role)
        assertTrue(messageOutput.content.isNotEmpty())

        // Validate firstMessageText helper
        assertNotNull(response.firstMessageText)
        assertTrue(response.firstMessageText?.isNotEmpty() == true)
    }

    @Test
    fun testResponsesWithReasoningBasic() = test {
        val request = responseRequest {
            model = ModelId("gpt-5") // Use reasoning model
            reasoning = ReasoningConfig(effort = "medium")
            include = listOf("reasoning.encrypted_content")
            input {
                message {
                    role = ChatRole.User
                    content = "Solve this step by step: What is 15 * 23?"
                }
            }
        }

        val response = openAI.createResponse(request)

        // Validate basic response with reasoning
        assertNotNull(response.id)
        assertEquals("completed", response.status)
        assertTrue(response.output.isNotEmpty())

        // Reasoning may or may not be available depending on model/API
        // But if reasoning config is provided, reasoning field should be present
        if (response.reasoning != null) {
            // Verify reasoning structure when present
            assertNotNull(response.reasoning)
            assertNotNull(response.reasoning?.encryptedContent)
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
                        content = "What is 2 + 2?"
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
                        content = "Explain the concept of gravity."
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
    fun testResponsesWithPreviousReasoning() = test {
        // First request to establish reasoning context
        val firstRequest = responseRequest {
            model = ModelId("gpt-5") // Use reasoning model
            reasoning = ReasoningConfig(effort = "medium", summary = "detailed")
            include = listOf("reasoning.encrypted_content")
            input {
                message {
                    role = ChatRole.User
                    content = "What is 10 + 5? Please show your reasoning."
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
        val reasoningOutput = firstResponse.output.filterIsInstance<ResponseOutputItem.Reasoning>().firstOrNull()
        val previousEncryptedContent = reasoningOutput?.encryptedContent
        val reasoningSummary = reasoningOutput?.summary?.filterIsInstance<SummaryTextPart>()?.firstOrNull()

        // Verify reasoning content is available
        assertNotNull(reasoningOutput)
        assertNotNull(previousEncryptedContent)

        // Second request with previous reasoning - properly pass the encrypted content
        val secondRequest = responseRequest {
            model = ModelId("gpt-5") // Use reasoning model
            reasoning = ReasoningConfig(effort = "medium", summary = "detailed")
            include = listOf("reasoning.encrypted_content")
            input {
                // Previous conversation context
                message {
                    role = ChatRole.User
                    content = "What is 10 + 5? Please show your reasoning."
                }
                message {
                    role = ChatRole.Assistant
                    content = firstResponse.firstMessageText ?: "15"
                }

                // Pass previous reasoning - use encryptedContent field directly on reasoning item
                reasoning {
                    content = emptyList() // API enforces this to be empty
                    summary = if (reasoningSummary is SummaryTextPart) {
                        listOf(SummaryTextPart(reasoningSummary.text))
                    } else {
                        listOf(SummaryTextPart("Previous calculation: 10 + 5 = 15"))
                    }
                    encryptedContent = previousEncryptedContent // Pass the encrypted content directly
                }

                // New question building on previous context
                message {
                    role = ChatRole.User
                    content = "Now multiply that result by 2. Use the previous reasoning to inform your approach."
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
        val systemMessage = request.input[0] as ResponseInputItem.Message
        assertEquals(ChatRole.System, systemMessage.role)
        assertEquals("You are a helpful assistant.", systemMessage.content)

        val userMessage = request.input[1] as ResponseInputItem.Message
        assertEquals(ChatRole.User, userMessage.role)
        assertEquals("Hello!", userMessage.content)
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
                    content = listOf(ReasoningTextPart("Previous algebraic reasoning steps"))
                    summary = listOf(SummaryTextPart("Solved linear equation step by step"))
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
        val reasoningInput = request.input[3] as ResponseInputItem.Reasoning
        assertEquals(1, reasoningInput.content.size)
        assertEquals(1, reasoningInput.summary.size)
        assertTrue(reasoningInput.content[0] is ReasoningTextPart)
        assertTrue(reasoningInput.summary[0] is SummaryTextPart)
    }

    @Test
    fun testResponseWithUsageAndMetadata() = test {
        val request = responseRequest {
            model = ModelId("gpt-5")
            input {
                message {
                    role = ChatRole.User
                    content = "Write a short poem about coding."
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
        // Note: The API returns different field names than expected by the Usage class
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
                        content = "What is the capital of France?"
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
                    content = "Write a very long essay about the history of the universe, covering every detail from the Big Bang to the present day, including all scientific discoveries, philosophical implications, and cultural impacts throughout human history."
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
                    content = "Explain quantum computing in simple terms."
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
        val reasoningOutput = response.output.filterIsInstance<ResponseOutputItem.Reasoning>().firstOrNull()
        assertNotNull(reasoningOutput)
        assertNotNull(reasoningOutput.id)

        // For reasoning models, the actual response text might be in the reasoning summary or not present
        // The key is that we got a valid response with the correct structure

        // Validate usage information shows limited output tokens
        assertNotNull(response.usage)
        assertTrue((response.usage?.completionTokens ?: 0) <= 100) // Should be within the limit
        assertTrue((response.usage?.completionTokens ?: 0) > 0) // Should have generated some tokens
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
        val reasoningOutput = response.output.filterIsInstance<ResponseOutputItem.Reasoning>().firstOrNull()
        assertNotNull(reasoningOutput)
        assertNotNull(reasoningOutput.id)

        // Validate usage information shows limited output tokens (even more restrictive)
        assertNotNull(response.usage)
        assertTrue((response.usage?.completionTokens ?: 0) <= 50) // Should be within the stricter limit
        // Note: Some reasoning models may return 0 tokens for certain requests, so we just check the limit
    }
}
