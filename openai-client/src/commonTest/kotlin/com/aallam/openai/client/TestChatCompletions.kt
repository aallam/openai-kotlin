package com.aallam.openai.client

import com.aallam.openai.api.chat.*
import com.aallam.openai.api.chat.ChatResponseFormat.Companion.jsonSchema
import com.aallam.openai.api.model.ModelId
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlin.coroutines.cancellation.CancellationException
import kotlin.test.*

class TestChatCompletions : TestOpenAI() {

    @Test
    fun chatCompletions() = test {
        val request = chatCompletionRequest {
            model = ModelId("gpt-3.5-turbo")
            messages {
                message {
                    role = ChatRole.System
                    content = "You are a helpful assistant.!"
                }
                message {
                    role = ChatRole.User
                    content = "Who won the world series in 2020?"
                }
                message {
                    role = ChatRole.Assistant
                    content = "The Los Angeles Dodgers won the World Series in 2020."
                }
                message {
                    role = ChatRole.User
                    content = "Where was it played?"
                }
            }
        }

        val completion = openAI.chatCompletion(request)
        assertTrue { completion.choices.isNotEmpty() }

        val results = mutableListOf<ChatCompletionChunk>()
        openAI.chatCompletions(request).onEach { results += it }.launchIn(this).join()

        assertNotEquals(0, results.size)
    }

    @Test
    fun chatCompletionsFunction() = test {
        val modelId = ModelId("gpt-3.5-turbo")
        val chatMessages = mutableListOf(
            ChatMessage(
                role = ChatRole.User,
                content = "What's the weather like in Boston?"
            )
        )

        val request = chatCompletionRequest {
            model = modelId
            messages = chatMessages
            tools {
                function(
                    name = "currentWeather",
                    parameters =
                    """
                    {
                      "type": "object",
                      "properties": {
                        "location": {
                          "type": "string",
                          "description": "The city and state, e.g. San Francisco, CA"
                        },
                        "unit": {
                          "type": "string",
                          "enum": [
                            "celsius",
                            "fahrenheit"
                          ]
                        }
                      },
                      "required": [
                        "location"
                      ]
                    }
                    """
                )
            }

            toolChoice = ToolChoice.function("currentWeather")
        }

        val response = openAI.chatCompletion(request)
        val message = response.choices.first().message
        val toolCall = message.toolCalls?.first() as? ToolCall.Function
        assertNotNull(toolCall)
        assertEquals("currentWeather", toolCall.function?.name)
    }

    @Test
    fun json() = test {
        val request = chatCompletionRequest {
            model = ModelId("gpt-3.5-turbo-1106")
            responseFormat = ChatResponseFormat.JsonObject
            messages {
                message {
                    role = ChatRole.System
                    content = "You are a helpful assistant.!"
                }
                message {
                    role = ChatRole.System
                    content = """All your answers should be a valid JSON, using the following JSON schema definition:
                        | { "type": "object", "properties": { "question": { "type": "string" }, "response": { "type": "string" } }, "required": ["question", "response"] }
                        """.trimMargin()
                }
                message {
                    role = ChatRole.User
                    content = "Who won the world cup in 1998?"
                }
            }
        }
        val response = openAI.chatCompletion(request)
        val content = response.choices.first().message.content.orEmpty()

        @Serializable
        data class Answer(val question: String? = null, val response: String? = null)

        val answer = Json.decodeFromString<Answer>(content)
        assertNotNull(answer.question)
        assertNotNull(answer.response)
    }

    @Test
    fun jsonSchema() = test {
        val schemaJson = JsonObject(mapOf(
            "type" to JsonPrimitive("object"),
            "properties" to JsonObject(mapOf(
                "question" to JsonObject(mapOf(
                    "type" to JsonPrimitive("string"),
                    "description" to JsonPrimitive("The question that was asked")
                )),
                "response" to JsonObject(mapOf(
                    "type" to JsonPrimitive("string"),
                    "description" to JsonPrimitive("The answer to the question")
                ))
            )),
            "required" to JsonArray(listOf(
                JsonPrimitive("question"),
                JsonPrimitive("response")
            ))
        ))

        val jsonSchema = JsonSchema(
            name = "AnswerSchema",
            schema = schemaJson,
            strict = true
        )

        val request = chatCompletionRequest {
            model = ModelId("gpt-4o-mini-2024-07-18")
            responseFormat = jsonSchema(jsonSchema)
            messages {
                message {
                    role = ChatRole.System
                    content = "You are a helpful assistant.!"
                }
                message {
                    role = ChatRole.System
                    content = """All your answers should be a valid JSON
                        """.trimMargin()
                }
                message {
                    role = ChatRole.User
                    content = "Who won the world cup in 1998?"
                }
            }
        }
        val response = openAI.chatCompletion(request)
        val content = response.choices.first().message.content.orEmpty()

        @Serializable
        data class Answer(val question: String? = null, val response: String? = null)

        val answer = Json.decodeFromString<Answer>(content)
        assertNotNull(answer.question)
        assertNotNull(answer.response)
    }

    @Ignore
    @Test
    fun logprobs() = test {
        val request = chatCompletionRequest {
            model = ModelId("gpt-3.5-turbo-0125")
            messages {
                message {
                    role = ChatRole.User
                    content = "What's the weather like in Boston?"
                }
            }
            logprobs = true
        }
        val response = openAI.chatCompletion(request)
        val logprobs = response.choices.first().logprobs
        assertNotNull(logprobs)
        assertEquals(response.usage!!.completionTokens, logprobs.content!!.size)
    }

    @Ignore
    @Test
    fun top_logprobs() = test {
        val expectedTopLogProbs = 5
        val request = chatCompletionRequest {
            model = ModelId("gpt-3.5-turbo-0125")
            messages {
                message {
                    role = ChatRole.User
                    content = "What's the weather like in Boston?"
                }
            }
            logprobs = true
            topLogprobs = expectedTopLogProbs
        }
        val response = openAI.chatCompletion(request)
        val logprobs = response.choices.first().logprobs
        assertNotNull(logprobs)
        assertEquals(response.usage!!.completionTokens, logprobs.content!!.size)
        assertEquals(logprobs.content!![0].topLogprobs.size, expectedTopLogProbs)
    }

    @Test
    fun cancellable() = test {
        val request = chatCompletionRequest {
            model = ModelId("gpt-3.5-turbo")
            messages {
                message {
                    role = ChatRole.System
                    content = "You are a helpful assistant.!"
                }
                message {
                    role = ChatRole.User
                    content = "Who won the world series in 2020?"
                }
            }
        }

        val job = launch {
            try {
                openAI.chatCompletions(request).collect()
            } catch (e: CancellationException) {
                println("Flow was cancelled as expected.")
            } catch (e: Exception) {
                fail("Flow threw an unexpected exception: ${e.message}")
            }
        }

        advanceTimeBy(1000)

        job.cancel()
        job.join()

        assertTrue(job.isCancelled, "Job should be cancelled")
    }

    @Test
    fun streamOptions() = test {
        val request = chatCompletionRequest {
            model = ModelId("gpt-3.5-turbo")
            messages {
                message {
                    role = ChatRole.System
                    content = "You are a helpful assistant.!"
                }
                message {
                    role = ChatRole.User
                    content = "Who won the world series in 2020?"
                }
                message {
                    role = ChatRole.Assistant
                    content = "The Los Angeles Dodgers won the World Series in 2020."
                }
                message {
                    role = ChatRole.User
                    content = "Where was it played?"
                }
            }
            streamOptions = streamOptions {
                includeUsage = true
            }
        }

        val results = mutableListOf<ChatCompletionChunk>()
        openAI.chatCompletions(request).onEach { results += it }.launchIn(this).join()

        assertNotNull(results.last().usage)
        assertNotNull(results.last().usage?.promptTokens)
        assertNotNull(results.last().usage?.completionTokens)
        assertNotNull(results.last().usage?.totalTokens)
    }
}
