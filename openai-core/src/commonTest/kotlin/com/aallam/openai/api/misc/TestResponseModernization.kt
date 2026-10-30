package com.aallam.openai.api.misc

import com.aallam.openai.api.chat.ChatResponseFormat
import com.aallam.openai.api.chat.JsonSchema
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.api.response.*
import kotlinx.serialization.json.*
import kotlin.test.*

class TestResponseModernization {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun structuredOutputUsesResponsesShapeWithoutChangingChat() {
        val schema = JsonSchema("answer", buildJsonObject { put("type", "object") }, true)
        val format = ChatResponseFormat.jsonSchema(schema)
        val request = ResponseCreateRequest(ModelId("model"), text = ResponseTextOptions(format))
        val wire = json.encodeToJsonElement(ResponseCreateRequest.serializer(), request).jsonObject
        val responseFormat = wire.getValue("text").jsonObject.getValue("format").jsonObject
        assertEquals(JsonPrimitive("answer"), responseFormat["name"])
        assertEquals(schema.schema, responseFormat["schema"])
        assertFalse("json_schema" in responseFormat)
        val chatFormat = json.encodeToJsonElement(ChatResponseFormat.serializer(), format).jsonObject
        assertEquals(JsonPrimitive("answer"), chatFormat["json_schema"]?.jsonObject?.get("name"))
        assertEquals(format, json.decodeFromJsonElement(ResponseCreateRequest.serializer(), wire).text?.format)
    }

    @Test
    fun legacySchemaFormatKeepsItsOriginalTypeAndSerializesFlat() {
        val schema = JsonSchema("answer", buildJsonObject { put("type", "object") }, true)
        val request = ResponseRequest(ModelId("model"), text = ResponseText(ChatResponseFormat.jsonSchema(schema)))
        val encoded = json.encodeToJsonElement(ResponseRequest.serializer(), request).jsonObject
        val format = encoded.getValue("text").jsonObject.getValue("format").jsonObject
        assertEquals("answer", format["name"]?.jsonPrimitive?.content)
        assertFalse("json_schema" in format)
        assertEquals(request, json.decodeFromJsonElement(ResponseRequest.serializer(), encoded))
    }

    @Test
    fun missingSchemaNameIsRejectedBeforeSending() {
        val request = ResponseCreateRequest(ModelId("model"), text = ResponseTextOptions(ChatResponseFormat.jsonSchema(JsonSchema(schema = buildJsonObject {}))))
        assertFailsWith<IllegalArgumentException> { json.encodeToJsonElement(ResponseCreateRequest.serializer(), request) }
    }

    @Test
    fun hostedToolsAndFileContextPreserveWireFields() {
        val request = ResponseCreateRequest(
            ModelId("model"),
            input = responseInput(listOf(ResponseInputEntry.message("user", listOf(ResponseInputContent("input_file", fileId = "file_1"))))),
            background = true,
            tools = listOf(ResponseToolDefinition("file_search", vectorStoreIds = listOf("vs_1")), ResponseToolDefinition("mcp", serverLabel = "remote", serverUrl = "https://example.com/mcp")),
            moderation = ResponseModeration("omni-moderation-latest", ResponseModerationPolicy(input = ResponseModerationMode("block"))),
        )
        val wire = json.encodeToJsonElement(ResponseCreateRequest.serializer(), request).jsonObject
        assertEquals("file_1", wire["input"]!!.jsonArray[0].jsonObject["content"]!!.jsonArray[0].jsonObject["file_id"]!!.jsonPrimitive.content)
        assertEquals("vs_1", wire["tools"]!!.jsonArray[0].jsonObject["vector_store_ids"]!!.jsonArray[0].jsonPrimitive.content)
        assertEquals("remote", wire["tools"]!!.jsonArray[1].jsonObject["server_label"]!!.jsonPrimitive.content)
        assertEquals("block", wire["moderation"]!!.jsonObject["policy"]!!.jsonObject["input"]!!.jsonObject["mode"]!!.jsonPrimitive.content)
    }

    @Test
    fun streamEventsPreserveUnknownFieldsAndTypedLifecycleData() {
        val payload = """{"type":"response.completed","sequence_number":8,"future":{"kept":true},"response":{"id":"resp_1","output":[{"type":"compaction","encrypted_content":"opaque"}]}}"""
        val event = json.decodeFromString(ResponseStreamEvent.serializer(), payload)
        assertEquals(8, event.sequenceNumber)
        assertEquals("opaque", event.data["response"]?.jsonObject?.get("output")?.jsonArray?.single()?.jsonObject?.get("encrypted_content")?.jsonPrimitive?.content)
        assertEquals(json.parseToJsonElement(payload), json.encodeToJsonElement(ResponseStreamEvent.serializer(), event))
    }

    @Test
    fun toolResultsAndApprovalsCanContinueConversation() {
        val item = ResponseInputEntry(type = "function_call_output", callId = "call_1", output = JsonPrimitive("result"))
        val input = json.encodeToJsonElement(ResponseInputEntry.serializer(), item).jsonObject
        assertEquals("call_1", input["call_id"]?.jsonPrimitive?.content)
        assertEquals("result", input["output"]?.jsonPrimitive?.content)
        val approval = ResponseInputEntry(type = "mcp_approval_response", approvalRequestId = "approval_1", approve = false)
        assertEquals(false, json.encodeToJsonElement(ResponseInputEntry.serializer(), approval).jsonObject["approve"]?.jsonPrimitive?.boolean)
    }

    @Test
    fun outputPayloadRetainsHostedToolsCitationsAndUnknownFutureFields() {
        val payload = """{"id":"resp_1","background":true,"service_tier":"default","future_metadata":{"kept":true},"output":[{"type":"mcp_call","server_label":"remote","output":"result","future_tool":{"kept":true}},{"type":"image_generation_call","result":"base64"},{"type":"reasoning","encrypted_content":"opaque"},{"type":"message","content":[{"type":"output_text","text":"answer","annotations":[{"type":"file_citation","file_id":"file_1","filename":"notes.txt","index":2,"container_id":"container_1"}]}]}]}"""
        val response = json.decodeFromString(ResponseResult.serializer(), payload)
        assertEquals(true, response.background)
        assertEquals("remote", response.output[0].serverLabel)
        assertEquals("result", response.output[0].output?.jsonPrimitive?.content)
        assertEquals("base64", response.output[1].result)
        assertEquals("opaque", response.output[2].encryptedContent)
        val citation = response.output[3].content?.single()?.annotations?.single()
        assertEquals("file_1", citation?.fileId)
        assertEquals("container_1", citation?.containerId)
        assertEquals(json.parseToJsonElement(payload).jsonObject, response.rawData)
        val encoded = json.encodeToJsonElement(ResponseResult.serializer(), response).jsonObject
        assertFalse("future_metadata" in encoded)
        assertFalse("future_tool" in encoded.getValue("output").jsonArray.first().jsonObject)
        val exported = json.parseToJsonElement(requireNotNull(response.rawData).toString()).jsonObject
        assertEquals(json.parseToJsonElement(payload).jsonObject, exported)
        val continuation = ResponseInput(exported.getValue("output"))
        assertEquals(true, continuation.value.jsonArray.first().jsonObject["future_tool"]?.jsonObject?.get("kept")?.jsonPrimitive?.boolean)
        val changed = response.copy(output = listOf(ResponseResultItem(type = "image_generation_call", result = "replacement")))
        val changedWire = json.encodeToJsonElement(ResponseResult.serializer(), changed).jsonObject
        assertEquals("replacement", changedWire.getValue("output").jsonArray.single().jsonObject["result"]?.jsonPrimitive?.content)
        assertFalse("future_tool" in changedWire.getValue("output").jsonArray.single().jsonObject)
        assertEquals(response.rawData, changed.rawData)
        assertEquals(true, response.rawData?.get("output")?.jsonArray?.get(0)?.jsonObject?.get("future_tool")?.jsonObject?.get("kept")?.jsonPrimitive?.boolean)
    }
}
