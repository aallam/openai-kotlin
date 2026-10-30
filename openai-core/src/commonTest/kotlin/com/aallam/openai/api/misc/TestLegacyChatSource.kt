package com.aallam.openai.api.misc

import com.aallam.openai.api.chat.*
import com.aallam.openai.api.model.ModelId
import kotlin.test.Test
import kotlin.test.assertEquals

/** These examples intentionally retain the original types and exhaustive branches. */
class TestLegacyChatSource {
    @Test
    fun legacyRequestPropertiesBuildersAndSealedTypesStillCompile() {
        val tools: List<Tool> = listOf(Tool(ToolType.Function, FunctionTool("lookup")))
        val request = chatCompletionRequest {
            model = ModelId("model")
            messages = listOf(ChatMessage.User("Hello"))
            this.tools = tools
        }
        val originalTools: List<Tool>? = request.tools
        assertEquals("lookup", originalTools?.single()?.function?.name)
        assertEquals("text", partKind(TextPart("hello")))
        assertEquals("function", callKind(ToolCall.Function(ToolId("call"), FunctionCall("lookup", "{}"))))
        assertEquals("mode", choiceKind(ToolChoice.Required))
    }

    private fun partKind(part: ContentPart): String = when (part) {
        is TextPart -> "text"
        is ImagePart -> "image"
    }

    private fun callKind(call: ToolCall): String = when (call) {
        is ToolCall.Function -> "function"
    }

    private fun choiceKind(choice: ToolChoice): String = when (choice) {
        is ToolChoice.Mode -> "mode"
        is ToolChoice.Named -> "named"
    }
}
