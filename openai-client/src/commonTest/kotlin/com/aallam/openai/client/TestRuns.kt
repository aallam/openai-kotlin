package com.aallam.openai.client

import com.aallam.openai.api.assistant.AssistantTool
import com.aallam.openai.api.assistant.assistantRequest
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.api.run.RunRequest
import kotlin.test.Test
import kotlin.test.assertEquals

class TestRuns : TestOpenAI() {

    @Test
    fun runs() = test {
        val assistant = openAI.assistant(
            request = assistantRequest {
                name = "Math Tutor"
                tools = listOf(AssistantTool.CodeInterpreter)
                model = ModelId("gpt-4")
            }
        )
        val thread = openAI.thread()
        val request = RunRequest(assistantId = assistant.id)
        val run = openAI.createRun(threadId = thread.id, request = request)
        assertEquals(thread.id, run.threadId)

        var retrieved = openAI.getRun(threadId = thread.id, runId = run.id)
        assertEquals(run.id, retrieved.id)

        val canceled = openAI.cancel(threadId = thread.id, runId = run.id)
        assertEquals(run.id, canceled.id)

        val metadata = mapOf("modified" to "true", "user" to "aallam")
        val modified = openAI.updateRun(threadId = thread.id, runId = run.id, metadata = metadata)
        assertEquals(metadata, modified.metadata)

        val runs = openAI.runs(threadId = thread.id)
        assertEquals(1, runs.size)
    }

    @Test
    fun threadAndRuns() = test {
        val assistant = openAI.assistant(
            request = assistantRequest {
                name = "Math Tutor"
                tools = listOf(AssistantTool.CodeInterpreter)
                model = ModelId("gpt-4")
            }
        )
        val request = RunRequest(assistantId = assistant.id)
        val run = openAI.createThreadRun(request)
        assertEquals(assistant.id, run.assistantId)

        val runs = openAI.runSteps(threadId = run.threadId, runId = run.id)
        assertEquals(0, runs.size)
    }
}
