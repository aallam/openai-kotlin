# Responses

Create a response with `response(ResponseRequest(...))`. Retrieve a stored response with `response(ResponseId(...))`, cancel a background response with `cancel(id)`, or delete it with `delete(id)`.

The returned response exposes hosted-tool results, metadata and file/container citations through its typed fields. `rawData` retains the complete original decoded payload, including nested fields introduced after this client version. It is a snapshot: `copy()` does not update it, and encoding a `Response` writes only its typed properties. Unknown fields are not preserved by typed decode/encode round trips.

To export every original field or continue with original output items, use the snapshot directly:

```kotlin
val original = requireNotNull(response.rawData)
val exportedJson = original.toString()
val originalOutput = ResponseInput(original.getValue("output"))
```

These values reflect the original payload, not edits made with `copy()`. When using edited typed fields, construct the intended continuation input explicitly.

For input-item pagination with additional tool or reasoning fields, use `responseInputItemsPage(id, include = listOf("reasoning.encrypted_content"))`. The original `responseInputItems` method and its inferred callable reference remain available.

## Structured output

The existing `ChatResponseFormat` type can configure Responses output. The client serializes JSON schemas in the flat Responses format while keeping Chat Completions serialization unchanged. A schema name is required.

```kotlin
val format = ChatResponseFormat.jsonSchema(
    JsonSchema(
        name = "answer",
        schema = buildJsonObject {
            put("type", "object")
            putJsonObject("properties") {
                putJsonObject("answer") { put("type", "string") }
            }
            putJsonArray("required") { add("answer") }
            put("additionalProperties", false)
        },
        strict = true,
    )
)
val result = openai.response(
    ResponseRequest(
        model = ModelId(modelName),
        input = ResponseInput("Explain Kotlin coroutines briefly."),
        text = ResponseText(format = format),
    )
)
```

## Files and tools

Use typed message content for uploaded files, image URLs and text. `ResponseInput(JsonElement)` remains available for input variants that do not yet have a dedicated model.

```kotlin
val input = responseInput(listOf(
    ResponseInputEntry.message("user", listOf(
        ResponseInputContent(type = "input_text", text = "Summarize this document."),
        ResponseInputContent(type = "input_file", fileId = uploadedFileId),
    ))
))
val result = openai.response(
    ResponseRequest(
        model = ModelId(modelName),
        input = input,
        tools = listOf(ResponseTool(type = "file_search", vectorStoreIds = listOf(vectorStoreId))),
    )
)
```

For MCP, provide `type = "mcp"`, `serverLabel`, and either `serverUrl` or the appropriate connector configuration. Tool-specific polymorphic settings such as approval policies, filters and container configurations accept JSON values. Do not log authorization tokens or credential-bearing headers.

Return function results using `ResponseInputEntry(type = "function_call_output", callId = callId, output = JsonPrimitive(result))`. Continue with `previousResponseId` or include the required earlier input items. MCP approvals use `approvalRequestId` and `approve`.

## Streaming

```kotlin
openai.responses(ResponseRequest(ModelId(modelName), ResponseInput("Hello")))
    .collect { event ->
        event.delta?.let { print(it) }
        event.response?.let { response -> /* lifecycle snapshot */ }
        if (event.type == "error") {
            // Inspect event.data for the API error code and message.
        }
    }
```

Events expose their discriminator, sequence number, text/argument delta, response and output item when supplied. `event.data` retains the entire payload, including tool-specific fields and future event kinds. HTTP errors use the client's existing exception handling; API errors delivered inside a successful event stream remain observable as events.

Cancelling collection closes the response body. To resume a stored background response, call `responses(id, startingAfter = lastSequenceNumber)`. Background processing must have been requested with `background = true`; closing the HTTP connection does not itself cancel background processing.

## Compaction and token estimates

`compactResponse(ResponseCompactRequest(...))` returns compacted output items and token usage. Its output preserves every item field as JSON. Continue with `ResponseInput(JsonArray(compaction.output))` to retain encrypted compaction and tool context.

`responseInputTokens(ResponseTokenCountRequest(...))` estimates input tokens without generating output. Include the same instructions, context, reasoning and tool settings used for the intended response.

Parameter availability depends on the selected model. See the [official Responses reference](https://developers.openai.com/api/reference/resources/responses/methods/create).

## WebSocket sessions

`responseSession` opens a scoped connection to `/v1/responses` using a WebSocket-capable Ktor engine. It retains the configured base path, explicit port, headers and request query options. A model is selected in each `create` call, not in a Realtime session configuration.

```kotlin
openai.responseSession {
    create(
        ResponseRequest(ModelId(modelName), ResponseInput("Hello")),
        streamId = "main",
    )
    events.takeWhile { it.type != "response.completed" }.collect { event ->
        event.delta?.let { print(it) }
        if (event.type == "error") {
            // Inspect event.data; API protocol errors are delivered as events.
        }
    }
}
```

`create` sends a `response.create` JSON event with the request fields at the top level. It omits HTTP transport fields and rejects `background = true`. `generate = false` warms up request state. Use `previousResponseId` and incremental inputs to continue a turn. `send(JsonObject)` supports other protocol events without discarding future fields.

Sends are serialized and may originate in concurrent child coroutines. Only one event collector may run at a time; dispatch interleaved events by `event.streamId` when using named lanes. For multiple turns, keep that collector running and send subsequent requests while it remains active. Stream IDs accept letters, numbers, periods, underscores and hyphens, with a maximum length of 256 characters.

Returning from the session block, an exception, or completing/cancelling event collection closes the connection. Keep all connection work inside the block. The client does not automatically replay turns after a connection failure. Reconnect and provide the appropriate prior response ID or complete input context; the service's connection-local state does not survive reconnection. HTTP handshake failures and transport failures propagate as exceptions; service error frames remain observable protocol events.

`secure = false` selects plain WebSockets for trusted local test servers; the default uses secure WebSockets. Platform support depends on the chosen Ktor engine; the API and event handling compile for JVM, JS and Wasm, while the actual transport is verified with a JVM loopback server.

See the [official WebSocket mode guide](https://developers.openai.com/api/docs/guides/websocket-mode) for current protocol limits and continuation semantics.
