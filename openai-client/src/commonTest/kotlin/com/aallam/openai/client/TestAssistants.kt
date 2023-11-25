package com.aallam.openai.client

import com.aallam.openai.api.assistant.AssistantTool
import com.aallam.openai.api.assistant.assistantRequest
import com.aallam.openai.api.model.ModelId
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TestAssistants : TestOpenAI() {

    @Test
    fun assistants() = test {
        val request = assistantRequest {
            name = "Math Tutor"
            tools = listOf(AssistantTool.CodeInterpreter)
            model = ModelId("gpt-4")
        }
        val assistant = openAI.assistant(request)
        assertEquals(request.name, assistant.name)
        assertEquals(request.tools, assistant.tools)
        assertEquals(request.model, assistant.model)

        val getAssistant = openAI.assistant(assistant.id)
        assertEquals(getAssistant, assistant)

        val assistants = openAI.assistants()
        assertTrue { assistants.isNotEmpty() }

        val updated = assistantRequest { name = "Super Math Tutor" }
        val updatedAssistant = openAI.assistant(assistant.id, updated)
        assertEquals(updated.name, updatedAssistant.name)

        openAI.delete(updatedAssistant.id)

        val fileGetAfterDelete = openAI.assistant(updatedAssistant.id)
        assertNull(fileGetAfterDelete)
    }
}
