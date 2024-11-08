package com.aallam.openai.client

import com.aallam.openai.api.assistant.AssistantTool
import com.aallam.openai.api.assistant.assistantRequest
import com.aallam.openai.api.chat.ToolCall
import com.aallam.openai.api.core.JsonSchema
import com.aallam.openai.api.core.ResponseFormat
import com.aallam.openai.api.core.Schema
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.api.run.RequiredAction
import com.aallam.openai.api.run.Run
import com.aallam.openai.client.internal.JsonLenient
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TestAssistants : TestOpenAI() {

    @Test
    fun codeInterpreter() = test {
        val request = assistantRequest {
            name = "Math Tutor"
            tools = listOf(AssistantTool.CodeInterpreter)
            model = ModelId("gpt-4o")
            responseFormat = ResponseFormat.TextResponseFormat
        }
        val assistant = openAI.assistant(
            request = request,
        )
        assertEquals(request.name, assistant.name)
        assertEquals(request.tools, assistant.tools)
        assertEquals(request.model, assistant.model)
        assertEquals(request.responseFormat, assistant.responseFormat)

        val getAssistant = openAI.assistant(
            assistant.id,
        )
        assertEquals(getAssistant, assistant)

        val assistants = openAI.assistants()
        assertTrue { assistants.isNotEmpty() }

        val updated = assistantRequest {
            name = "Super Math Tutor"
            responseFormat = ResponseFormat.AutoResponseFormat
        }
        val updatedAssistant = openAI.assistant(
            assistant.id,
            updated,
        )
        assertEquals(updated.name, updatedAssistant.name)
        assertEquals(request.responseFormat, assistant.responseFormat)

        openAI.delete(
            updatedAssistant.id,
        )

        val fileGetAfterDelete = openAI.assistant(
            updatedAssistant.id,
        )
        assertNull(fileGetAfterDelete)
    }

    @Test
    fun functionCall() {
        val json = """
            {
              "id": "run_xxxxxx",
              "object": "thread.run",
              "created_at": 1700939516,
              "assistant_id": "asst_xxxx",
              "thread_id": "thread_xxxxx",
              "status": "requires_action",
              "started_at": 1700939516,
              "expires_at": 1700940116,
              "cancelled_at": null,
              "failed_at": null,
              "completed_at": null,
              "required_action": {
                "type": "submit_tool_outputs",
                "submit_tool_outputs": {
                  "tool_calls": [
                    {
                      "id": "call_d0SQRcipynYAoc3k5wAkrTta",
                      "type": "function",
                      "function": {
                        "name": "executeAgentTask",
                        "arguments": "{\n  \"taskType\": \"ADD\",\n  \"taskDestination\": \"CALENDAR\",\n  \"taskData\": \"18:00 - Wizyta u weterynarza\"\n}"
                      }
                    }
                  ]
                }
              },
              "last_error": null,
              "model": "gpt-3.5-turbo",
              "instructions": "Please address the user as Jane Doe. The user has a premium account.",
              "tools": [
                {
                  "type": "function",
                  "function": {
                    "name": "executeAgentTask",
                    "description": "Service for executing assistant tasks",
                    "parameters": {
                      "type": "object",
                      "properties": {
                        "taskType": {
                          "type": "string",
                          "enum": [
                            "ADD",
                            "DELETE",
                            "UPDATE",
                            "GET"
                          ],
                          "description": "Task type: ADD, DELETE, UPDATE, GET"
                        },
                        "taskDestination": {
                          "type": "string",
                          "enum": [
                            "CALENDAR",
                            "NOTES",
                            "REMINDERS"
                          ],
                          "description": "Task destination: CALENDAR, NOTES, REMINDERS"
                        },
                        "taskData": {
                          "type": "string",
                          "description": "Description of the task to be executed"
                        }
                      },
                      "required": [
                        "taskType",
                        "taskDestination",
                        "taskData"
                      ]
                    }
                  }
                }
              ],
              "file_ids": [],
              "metadata": {}
            }
        """.trimIndent()

        val decoded = JsonLenient.decodeFromString<Run>(json)
        assertIs<AssistantTool.FunctionTool>(decoded.tools?.first())
        assertIs<RequiredAction.SubmitToolOutputs>(decoded.requiredAction)
        val action = decoded.requiredAction as RequiredAction.SubmitToolOutputs
        assertIs<ToolCall.Function>(action.toolOutputs.toolCalls.first())
    }

    @Test
    fun jsonSchemaAssistant() = test {
        val jsonSchema = ResponseFormat.JsonSchemaResponseFormat(
            schema = JsonSchema(
                name = "TestSchema",
                description = "A test schema",
                schema = Schema.buildJsonObject {
                    put("type", "object")
                    put("properties", buildJsonObject {
                        put("name", buildJsonObject {
                            put("type", "string")
                        })
                    })
                    put("required", JsonArray(listOf(JsonPrimitive("name"))))
                    put("additionalProperties", false)
                },
                strict = true
            )
        )

        val request = assistantRequest {
            name = "Schema Assistant"
            model = ModelId("gpt-4o-mini")
            responseFormat = jsonSchema
        }

        val assistant = openAI.assistant(
            request = request,
        )
        assertEquals(request.name, assistant.name)
        assertEquals(request.model, assistant.model)
        assertEquals(request.responseFormat, assistant.responseFormat)

        val getAssistant = openAI.assistant(
            assistant.id,
        )
        assertEquals(getAssistant, assistant)

        val assistants = openAI.assistants()
        assertTrue { assistants.isNotEmpty() }

        val updated = assistantRequest {
            name = "Updated Schema Assistant"
            responseFormat = ResponseFormat.AutoResponseFormat
        }
        val updatedAssistant = openAI.assistant(
            assistant.id,
            updated,
        )
        assertEquals(updated.name, updatedAssistant.name)
        assertEquals(updated.responseFormat, updatedAssistant.responseFormat)

        openAI.delete(
            updatedAssistant.id,
        )

        val fileGetAfterDelete = openAI.assistant(
            updatedAssistant.id,
        )
        assertNull(fileGetAfterDelete)
    }
}
