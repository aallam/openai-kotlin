package com.aallam.openai.client

import com.aallam.openai.api.assistant.AssistantTool
import com.aallam.openai.api.assistant.assistantRequest
import com.aallam.openai.api.core.PaginatedList
import com.aallam.openai.api.core.Role
import com.aallam.openai.api.core.Status
import com.aallam.openai.api.exception.OpenAIAPIException
import com.aallam.openai.api.message.MessageRequest
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.api.run.AssistantStreamEvent
import com.aallam.openai.api.run.AssistantStreamEventType
import com.aallam.openai.api.run.Run
import com.aallam.openai.api.run.RunRequest
import com.aallam.openai.api.run.RunStep
import com.aallam.openai.api.run.ThreadRunRequest
import com.aallam.openai.api.thread.Thread
import com.aallam.openai.api.thread.ThreadId
import com.aallam.openai.api.thread.ThreadMessage
import com.aallam.openai.api.thread.ThreadRequest
import com.aallam.openai.client.extension.getData
import com.aallam.openai.client.internal.JsonLenient
import com.aallam.openai.client.internal.TestCleanup
import com.aallam.openai.client.internal.reportUsage
import com.aallam.openai.client.internal.withTestCleanup
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.singleOrNull
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class TestRuns : TestOpenAI() {

    @Test
    fun runs() = test {
        withTestCleanup {
            val assistant = openAI.assistant(
                request = assistantRequest {
                    name = "Math Tutor"
                    tools = listOf(AssistantTool.CodeInterpreter)
                    model = ModelId("gpt-4o")
                }
            )
            afterTest { assertTrue(openAI.delete(assistant.id)) }
            val cleanup = RunCleanup(this, openAI)
            val thread = openAI.thread()
            cleanup.thread(thread.id)
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
            cleanup.run(run)
            assertEquals(thread.id, run.threadId)

            var retrieved = openAI.getRun(threadId = thread.id, runId = run.id)
            assertEquals(run.id, retrieved.id)

            val runs = openAI.runs(threadId = thread.id)
            assertEquals(1, runs.size)
        }
    }

    @Test
    fun streamingRuns() = test {
        withTestCleanup {
            val assistant = openAI.assistant(
                request = assistantRequest {
                    name = "Math Tutor"
                    tools = listOf(AssistantTool.CodeInterpreter)
                    model = ModelId("gpt-4o")
                }
            )
            afterTest { assertTrue(openAI.delete(assistant.id)) }
            val cleanup = RunCleanup(this, openAI)
            val thread = openAI.thread()
            cleanup.thread(thread.id)
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

            val runCompletedEvent = openAI
                .createStreamingRun(threadId = thread.id, request = request)
                .onEach(cleanup::event)
                .filter { it.type == AssistantStreamEventType.THREAD_RUN_COMPLETED }
                .singleOrNull()

            assertNotNull(runCompletedEvent)

            val run = runCompletedEvent.getData<Run>()
            reportUsage("assistant-run", run.model, run.usage)

            assertEquals(thread.id, run.threadId)

            var retrieved = openAI.getRun(threadId = thread.id, runId = run.id)
            assertEquals(run.id, retrieved.id)

            val runs = openAI.runs(threadId = thread.id)
            assertEquals(1, runs.size)
        }
    }

    @Test
    fun threadAndRuns() = test {
        withTestCleanup {
            val assistant = openAI.assistant(
                request = assistantRequest {
                    name = "Math Tutor"
                    tools = listOf(AssistantTool.CodeInterpreter)
                    model = ModelId("gpt-4o")
                }
            )
            afterTest { assertTrue(openAI.delete(assistant.id)) }
            val cleanup = RunCleanup(this, openAI)
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
            cleanup.run(run)
            assertEquals(assistant.id, run.assistantId)

            val runs = openAI.runSteps(threadId = run.threadId, runId = run.id)
            assertEquals(0, runs.size)
        }
    }

    @Test
    fun streamingThreadAndRuns() = test {
        withTestCleanup {
            val assistant = openAI.assistant(
                request = assistantRequest {
                    name = "Math Tutor"
                    tools = listOf(AssistantTool.CodeInterpreter)
                    model = ModelId("gpt-4o")
                }
            )
            afterTest { assertTrue(openAI.delete(assistant.id)) }
            val cleanup = RunCleanup(this, openAI)
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

            val runCompletedEvent = openAI
                .createStreamingThreadRun(request = request)
                .onEach(cleanup::event)
                .filter { it.type == AssistantStreamEventType.THREAD_RUN_COMPLETED }
                .singleOrNull()

            assertNotNull(runCompletedEvent)

            val run = runCompletedEvent.getData<Run>()
            reportUsage("assistant-run", run.model, run.usage)

            assertEquals(assistant.id, run.assistantId)

            val runs = openAI.runSteps(threadId = run.threadId, runId = run.id)
            assertEquals(1, runs.size)
        }
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


internal class RunCleanup(private val cleanup: TestCleanup, private val client: OpenAI) {
    private var threadId: ThreadId? = null
    private var latestRun: Run? = null

    fun thread(id: ThreadId) {
        if (threadId != null) return
        threadId = id
        cleanup.afterTest { assertTrue(client.delete(id)) }
    }

    fun run(run: Run) {
        thread(run.threadId)
        if (latestRun == null) cleanup.afterTest { cancelUnfinished(requireNotNull(latestRun)) }
        latestRun = run
    }

    fun event(event: AssistantStreamEvent) {
        when (event.type.dataType) {
            Thread::class -> thread(event.getData<Thread>().id)
            Run::class -> run(event.getData())
        }
    }

    private suspend fun cancelUnfinished(run: Run) = withContext(Dispatchers.Default) {
        withTimeout(10_000) {
            var latest = run
            while (!isTerminal(latest)) {
                if (latest.status != Status.Queued && latest.status != Status.Cancelling) {
                    try {
                        latest = client.cancel(latest.threadId, latest.id)
                    } catch (cause: OpenAIAPIException) {
                        if (cause.statusCode != 400) throw cause
                        latest = client.getRun(latest.threadId, latest.id)
                        if (!isTerminal(latest)) throw cause
                    }
                }
                if (!isTerminal(latest)) {
                    delay(100)
                    latest = client.getRun(latest.threadId, latest.id)
                }
            }
        }
    }

    private fun isTerminal(run: Run): Boolean = run.status.value in
        listOf("completed", "cancelled", "failed", "expired", "incomplete")
}
