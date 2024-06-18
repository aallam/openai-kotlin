package com.aallam.openai.client

import com.aallam.openai.api.assistant.AssistantTool
import com.aallam.openai.api.assistant.assistantRequest
import com.aallam.openai.api.core.PaginatedList
import com.aallam.openai.api.core.Role
import com.aallam.openai.api.message.MessageRequest
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.api.run.RunRequest
import com.aallam.openai.api.run.RunStep
import com.aallam.openai.api.run.ThreadRunRequest
import com.aallam.openai.api.thread.ThreadMessage
import com.aallam.openai.api.thread.ThreadRequest
import com.aallam.openai.client.internal.JsonLenient
import kotlin.test.Test
import kotlin.test.assertEquals

class TestRuns : TestOpenAI() {

    @Test
    fun runs() = test {
        val assistant = openAI.assistant(
            request = assistantRequest {
                name = "Math Tutor"
                tools = listOf(AssistantTool.CodeInterpreter)
                model = ModelId("gpt-4o")
            }
        )
        val thread = openAI.thread()
        val request = RunRequest(assistantId = assistant.id)
        openAI.message(
            threadId = thread.id,
            request = MessageRequest(
                role = Role.User,
                content = "solve me 1 + 1",
                metadata = mapOf(),
            ),
            requestOptions = null,
        )
        val run = openAI.createRun(threadId = thread.id, request = request)
        assertEquals(thread.id, run.threadId)

        var retrieved = openAI.getRun(threadId = thread.id, runId = run.id)
        assertEquals(run.id, retrieved.id)

        val runs = openAI.runs(threadId = thread.id)
        assertEquals(1, runs.size)
    }

    @Test
    fun threadAndRuns() = test {
        val assistant = openAI.assistant(
            request = assistantRequest {
                name = "Math Tutor"
                tools = listOf(AssistantTool.CodeInterpreter)
                model = ModelId("gpt-4o")
            }
        )
        val request = ThreadRunRequest(
            thread = ThreadRequest(
                listOf(
                    ThreadMessage(
                        role = Role.User,
                        content = "solve 1 + 2",
                    )
                )
            ),
            assistantId = assistant.id,
        )
        val run = openAI.createThreadRun(request)
        assertEquals(assistant.id, run.assistantId)

        val runs = openAI.runSteps(threadId = run.threadId, runId = run.id)
        assertEquals(0, runs.size)
    }

    @Test
    fun json() = test {
        val json = """
            {
              "object": "list",
              "data": [
                {
                  "id": "step_dVK6IlRuFv1z2d8GtB13a8Ff",
                  "object": "thread.run.step",
                  "created_at": 1700903454,
                  "run_id": "run_Sc9g8odBSrMHXFajgIRD1vx6",
                  "assistant_id": "asst_1UUuuAqyn7mctRo1bf95YV4G",
                  "thread_id": "thread_R6mlKFGLXAz71vb05JsEPeCf",
                  "type": "message_creation",
                  "status": "completed",
                  "cancelled_at": null,
                  "completed_at": 1700903455,
                  "expires_at": null,
                  "failed_at": null,
                  "last_error": null,
                  "step_details": {
                    "type": "message_creation",
                    "message_creation": {
                      "message_id": "msg_h9QzSO20zNuonZhXcwNpbnoR"
                    }
                  }
                },
                {
                  "id": "step_kDMRs0mJcg2bN7KzrcxorXI2",
                  "object": "thread.run.step",
                  "created_at": 1700903443,
                  "run_id": "run_Sc9g8odBSrMHXFajgIRD1vx6",
                  "assistant_id": "asst_1UUuuAqyn7mctRo1bf95YV4G",
                  "thread_id": "thread_R6mlKFGLXAz71vb05JsEPeCf",
                  "type": "tool_calls",
                  "status": "completed",
                  "cancelled_at": null,
                  "completed_at": 1700903454,
                  "expires_at": null,
                  "failed_at": null,
                  "last_error": null,
                  "step_details": {
                    "type": "tool_calls",
                    "tool_calls": [
                      {
                        "id": "call_MeXdlXsLiVDZGmjgtdzDDoKs",
                        "type": "code_interpreter",
                        "code_interpreter": {
                          "input": "from sympy import symbols, Eq, solve\r\n\r\n# Define the variable\r\nx = symbols('x')\r\n\r\n# Define the equation\r\nequation = Eq(3 * x + 11, 14)\r\n\r\n# Solve the equation\r\nsolution = solve(equation, x)\r\nsolution",
                          "outputs": [
                            {
                              "type": "logs",
                              "logs": "[1]"
                            }
                          ]
                        }
                      }
                    ]
                  }
                }
              ],
              "first_id": "step_dVK6IlRuFv1z2d8GtB13a8Ff",
              "last_id": "step_kDMRs0mJcg2bN7KzrcxorXI2",
              "has_more": false
            }
        """.trimIndent()
        val decoded = JsonLenient.decodeFromString<PaginatedList<RunStep>>(json)
        println(decoded)
    }
}
