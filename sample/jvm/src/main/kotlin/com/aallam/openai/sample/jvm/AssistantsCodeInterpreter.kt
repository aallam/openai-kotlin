package com.aallam.openai.sample.jvm

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.assistant.AssistantRequest
import com.aallam.openai.api.assistant.AssistantTool
import com.aallam.openai.api.core.Role
import com.aallam.openai.api.core.Status
import com.aallam.openai.api.message.MessageContent
import com.aallam.openai.api.message.MessageRequest
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.api.run.RunRequest
import com.aallam.openai.client.OpenAI
import kotlinx.coroutines.delay

@OptIn(BetaOpenAI::class)
suspend fun assistantsCodeInterpreter(openAI: OpenAI) {
    // 1. Create an Assistant
    val assistant = openAI.assistant(
        request = AssistantRequest(
            name = "Math Tutor",
            instructions = "You are a personal math tutor. Write and run code to answer math questions.",
            tools = listOf(AssistantTool.CodeInterpreter),
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
            content = "I need to solve the equation `3x + 11 = 14`. Can you help me?"
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
        thread.id,
        request = RunRequest(
            assistantId = assistant.id,
            instructions = "Please address the user as Jane Doe. The user has a premium account.",
        )
    )

    // 5. Check the run status
    do {
        delay(1500)
        val retrievedRun = openAI.getRun(threadId = thread.id, runId = run.id)
    } while (retrievedRun.status != Status.Completed)

    // 5.1 Check run steps
    val runSteps = openAI.runSteps(threadId = run.threadId, runId = run.id)
    println("\nRun steps: ${runSteps.size}")

    // 6. Display the assistant's response
    val assistantMessages = openAI.messages(thread.id)
    println("\nThe assistant's response:")
    for (message in assistantMessages) {
        val textContent = message.content.first() as? MessageContent.Text ?: error("Expected MessageContent.Text")
        println(textContent.text.value)
    }
}
