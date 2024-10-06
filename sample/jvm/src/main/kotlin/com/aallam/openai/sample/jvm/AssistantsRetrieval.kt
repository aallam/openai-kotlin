package com.aallam.openai.sample.jvm

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.assistant.AssistantRequest
import com.aallam.openai.api.assistant.AssistantTool
import com.aallam.openai.api.core.Role
import com.aallam.openai.api.core.Status
import com.aallam.openai.api.file.FileSource
import com.aallam.openai.api.file.FileUpload
import com.aallam.openai.api.file.Purpose
import com.aallam.openai.api.message.MessageContent
import com.aallam.openai.api.message.MessageRequest
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.api.run.RunRequest
import com.aallam.openai.client.OpenAI
import kotlinx.coroutines.delay
import kotlinx.io.files.Path

@OptIn(BetaOpenAI::class)
suspend fun assistantsRetrieval(openAI: OpenAI) {

    // 1. Upload a file with an "assistants" purpose
    val fileUpload = FileUpload(file = FileSource(Path("udhr.pdf")), purpose = Purpose("assistants"))
    val knowledgeBase = openAI.file(request = fileUpload)

    val assistant = openAI.assistant(
        request = AssistantRequest(
            name = "Human Rights Bot",
            instructions = "You are a chatbot specialized in 'The Universal Declaration of Human Rights.' Answer questions and provide information based on this document.",
            tools = listOf(AssistantTool.FileSearch),
            model = ModelId("gpt-4-1106-preview"),
        )
    )

    // 2. Create a thread
    val thread = openAI.thread()

    // 3. Add a message to the thread
    openAI.message(
        threadId = thread.id, request = MessageRequest(
            role = Role.User,
            content = "Can you explain the right to freedom of opinion and expression as stated in The Universal Declaration of Human Rights?"
        )
    )

    // 4. Run the assistant
    val run = openAI.createRun(
        thread.id, request = RunRequest(
            assistantId = assistant.id,
            instructions = "Provide a concise explanation of the right to freedom of opinion and expression.",
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

    openAI.delete(fileId = knowledgeBase.id)
}
