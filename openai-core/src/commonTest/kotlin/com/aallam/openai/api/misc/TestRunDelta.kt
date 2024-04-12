package com.aallam.openai.api.misc

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.assistant.AssistantTool
import com.aallam.openai.api.run.MessageCreationStep
import com.aallam.openai.api.run.Run
import com.aallam.openai.api.run.RunStep
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class TestRunDelta {

    @OptIn(BetaOpenAI::class)
    @Test
    fun testRun() {
        val json = """
            {
                "id": "run_KmEb5mnoSFX5H0MUdrG0EFop",
                "object": "thread.run",
                "created_at": 1712952916,
                "assistant_id": "asst_ZShFck6ZB4YVVl7dVNu6HkhT",
                "thread_id": "thread_UWJgU7o1K4y2iyytmljHlEbW",
                "status": "queued",
                "started_at": null,
                "expires_at": 1712953516,
                "cancelled_at": null,
                "failed_at": null,
                "completed_at": null,
                "required_action": null,
                "last_error": null,
                "model": "gpt-4-turbo-preview",
                "instructions": "Please address the user as Jane Doe. The user has a premium account.",
                "tools": [
                    {
                        "type": "code_interpreter"
                    }
                ],
                "file_ids": [],
                "metadata": {},
                "temperature": 1,
                "max_completion_tokens": null,
                "max_prompt_tokens": null,
                "truncation_strategy": {
                    "type": "auto",
                    "last_messages": null
                },
                "incomplete_details": null,
                "usage": null,
                "response_format": "auto",
                "tool_choice": "auto"
            }
        """.trimIndent()

        val runDelta = JsonLenient.decodeFromString(Run.serializer(), json)
        assertEquals("run_KmEb5mnoSFX5H0MUdrG0EFop", runDelta.id.id)
        assertEquals(1712952916, runDelta.createdAt)
        assertEquals("asst_ZShFck6ZB4YVVl7dVNu6HkhT", runDelta.assistantId.id)
        assertEquals("thread_UWJgU7o1K4y2iyytmljHlEbW", runDelta.threadId.id)
        assertEquals("queued", runDelta.status.value)
        assertIs<AssistantTool.CodeInterpreter>(runDelta.tools?.first())
    }

    @OptIn(BetaOpenAI::class)
    @Test
    fun testRunStep() {
        val json = """
            {
                "id": "step_psxubysrRDWRL2HxRhEV2Xwg",
                "object": "thread.run.step",
                "created_at": 1712953655,
                "run_id": "run_3CofhV7s4VTbrPmMXjtmtOGk",
                "assistant_id": "asst_YUepBjqI7hkbnjl37AcSZNYx",
                "thread_id": "thread_VRRPworPKTCh7jNsofqUEH2M",
                "type": "message_creation",
                "status": "in_progress",
                "cancelled_at": null,
                "completed_at": null,
                "expires_at": 1712954254,
                "failed_at": null,
                "last_error": null,
                "step_details": {
                    "type": "message_creation",
                    "message_creation": {
                        "message_id": "msg_cyQphb0jh8sakOIGn2RhP8G0"
                    }
                },
                "usage": null
            }
        """.trimIndent()

        val runStep = JsonLenient.decodeFromString(RunStep.serializer(), json)
        assertIs<MessageCreationStep>(runStep)
        assertEquals("step_psxubysrRDWRL2HxRhEV2Xwg", runStep.id.id)
        assertEquals(1712953655, runStep.createdAt)
        assertEquals("run_3CofhV7s4VTbrPmMXjtmtOGk", runStep.runId.id)
        assertEquals("asst_YUepBjqI7hkbnjl37AcSZNYx", runStep.assistantId.id)
        assertEquals("thread_VRRPworPKTCh7jNsofqUEH2M", runStep.threadId.id)
        assertEquals("msg_cyQphb0jh8sakOIGn2RhP8G0", runStep.stepDetails.messageCreation.messageId.id)
    }
}
