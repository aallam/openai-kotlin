package com.aallam.openai.sample.jvm

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.assistant.AssistantRequest
import com.aallam.openai.api.assistant.AssistantTool
import com.aallam.openai.api.assistant.Function
import com.aallam.openai.api.chat.ToolCall
import com.aallam.openai.api.core.Parameters
import com.aallam.openai.api.core.Role
import com.aallam.openai.api.core.Status
import com.aallam.openai.api.message.MessageContent
import com.aallam.openai.api.message.MessageRequest
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.api.run.RequiredAction
import com.aallam.openai.api.run.Run
import com.aallam.openai.api.run.RunRequest
import com.aallam.openai.api.run.ToolOutput
import com.aallam.openai.client.OpenAI
import kotlinx.coroutines.delay
import kotlinx.serialization.json.add
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject

@OptIn(BetaOpenAI::class)
suspend fun assistantsFunctions(openAI: OpenAI) {
    // 1. Create an Assistant
    val assistant = openAI.assistant(
        request = AssistantRequest(
            name = "Math Tutor",
            instructions = "You are a weather bot. Use the provided functions to answer questions.",
            tools = listOf(
                AssistantTool.FunctionTool(
                    function = Function(
                        name = "currentWeather",
                        description = "Get the weather for a location.",
                        parameters = Parameters.buildJsonObject {
                            put("type", "object")
                            putJsonObject("properties") {
                                putJsonObject("location") {
                                    put("type", "string")
                                    put("description", "The city and state e.g. San Francisco, CA")
                                }
                                putJsonObject("unit") {
                                    put("type", "string")
                                    putJsonArray("enum") {
                                        add("c")
                                        add("f")
                                    }
                                }
                            }
                            putJsonArray("required") {
                                add("location")
                            }
                        }
                    )
                ),
                AssistantTool.FunctionTool(
                    function = Function(
                        name = "nickname",
                        description = "Get the nickname of a city",
                        parameters = Parameters.buildJsonObject {
                            put("type", "object")
                            putJsonObject("properties") {
                                putJsonObject("location") {
                                    put("type", "string")
                                    put("description", "The city and state e.g. San Francisco, CA")
                                }
                            }
                            putJsonArray("required") {
                                add("location")
                            }
                        }
                    )
                )
            ),
            model = ModelId("gpt-4-1106-preview")
        )
    )

    // 2. Create a thread
    val thread = openAI.thread()

    // 3. Add a message to the thread
    openAI.message(
        threadId = thread.id,
        request = MessageRequest(
            role = Role.User,
            content = "What's the current weather in San Francisco, and what is its nickname?"
        )
    )
    val messages = openAI.messages(thread.id)
    println("List of messages in the thread:")
    for (message in messages) {
        val textContent = message.content.first() as? MessageContent.Text ?: error("Expected MessageContent.Text")
        println(textContent.text.value)
    }

    // 4. Run the assistant
    val run = openAI.createRun(
        threadId = thread.id,
        request = RunRequest(assistantId = assistant.id)
    )

    // 5. Check the run status
    var retrievedRun: Run
    do {
        delay(1500)
        retrievedRun = openAI.getRun(threadId = thread.id, runId = run.id)
    } while (retrievedRun.status != Status.RequiresAction)

    val toolResponses = mutableListOf<ToolOutput>()
    retrievedRun.requiredAction?.let { requiredAction ->
        val toolOutputs = when (requiredAction) {
            is RequiredAction.SubmitToolOutputs -> requiredAction.toolOutputs
        }
        for (toolCall in toolOutputs.toolCalls) {
            val response = when (toolCall) {
                is ToolCall.Function -> toolCall.execute()
            }
            toolResponses += ToolOutput(toolCallId = toolCall.id, output = response)
        }
    }

    retrievedRun = openAI.submitToolOutput(threadId = run.threadId, runId = run.id, output = toolResponses)
    while (retrievedRun.status != Status.Completed) {
        delay(1500)
        retrievedRun = openAI.getRun(threadId = thread.id, runId = run.id)
    }

    // 5.1 Check run steps
    val runSteps = openAI.runSteps(threadId = run.threadId, runId = run.id)
    println(retrievedRun)
    println("\nRun steps: ${runSteps.size}")

    // 6. Display the assistant's response
    val assistantMessages = openAI.messages(thread.id)
    println("\nThe assistant's response:")
    when (val message = assistantMessages.first().content.first()) {
        is MessageContent.Text -> println(message.text.value)
        else -> error("Expected MessageContent.Text")
    }
}
