# API migration

OpenAI endpoint availability and library source compatibility are separate. The library keeps existing interfaces and call patterns available; retaining an interface does not make its upstream endpoint available.

The changes in this modernization are additive for existing Kotlin callers. Existing public property types, builders, sealed variants and interface implementations must remain usable. Binary compatibility is maintained where practical; consumers should recompile when upgrading. Removing existing public APIs requires a separate, explicit compatibility decision.

## Upgrading from 4.1.0 to 4.2.0

Update the client, core and optional BOM dependencies to `4.2.0`. Existing constructors, data-class copy functions, property types and legacy method names remain available. Recompile applications when upgrading: generated serialization implementation classes can change even when Kotlin source calls remain compatible.

New request controls and richer results use additive APIs:

| Existing API | Additional capabilities |
| --- | --- |
| `response` | `createResponse`, `retrieveResponse`, `streamResponses` and current Responses request/result types. |
| `chatCompletion`, `chatCompletions` | `createChatCompletion`, `streamChatCompletions`, options and richer results; stored completion lifecycle methods. |
| `transcription`, `speech` | `transcribe`, `createSpeech` and streaming audio results. |
| `embeddings` | `createEmbeddings` with token inputs and encoded output formats. |
| `moderations` | `moderate` with text and image inputs. |

Existing custom client implementations inherit default unsupported implementations for new operations. Implement those operations before calling them on a custom client. The built-in `OpenAI` client provides the new application APIs; organization administration uses the separate `OpenAIAdmin` client with an explicit Admin API key.

Deprecated entry points remain callable and produce compiler warnings. Their upstream availability is independent of the library upgrade; the migration paths below cover retired endpoints.

### Availability and validation limits

Live validation covers the general JVM suite, Decisions, Skills, a primary Live connection and image provenance checks. Live SIP calls, recordings and media flows require separate application validation. Administration was verified offline without an Admin key; voice enrollment was not exercised without legitimate consent recordings. Containers lifecycle validation requires an eligible project and was blocked by the configured UK project's regional availability.

JVM transport validation does not establish browser or Native WebSocket engine behavior. Validate the engine, authentication and media stack used by your application. Nested evolving Responses payloads retain JSON escape hatches, and asymmetric webhook `v1a` verification is not included in this release. See the [Responses](Responses.md), [Realtime](Realtime.md), [Live](Live.md) and [Webhooks](Webhooks.md) guides for their contracts and limits.

## Choose a supported path

| Existing integration | Direction |
| --- | --- |
| Assistants, Threads, Messages, Runs | Use Responses with explicit input history or `previous_response_id`; Conversations provides persisted conversation/item management. |
| DALL-E generation/editing and image variations | Use current GPT Image generation/editing. GPT Image returns base64 image data and does not accept legacy `response_format`. Variations have no direct GPT Image equivalent. |
| `text-moderation-*` aliases | Select an available omni-moderation model, or omit the model to use OpenAI's current default. |
| `/fine-tunes` | The newer `/fine_tuning/jobs` API has separate eligibility and retirement constraints. Check current availability before investing in a new training workflow. |
| `/edits` | Use Responses or Chat Completions with editing instructions. |
| Legacy Completions | Prefer Responses for new work. A retired model does not by itself mean that the Completions endpoint has been removed. |
| Older speech/transcription integrations | Check the current audio retirement schedule and Realtime/Live migration guidance before choosing new models or protocols. |

The [OpenAI deprecation schedule](https://developers.openai.com/api/docs/deprecations) and [Assistants migration guide](https://developers.openai.com/api/docs/assistants/migration) are the authority for upstream timelines. Vector Stores remain an independent resource; do not remove vector retrieval when migrating away from Assistants. Deleting a fine-tuned model is also distinct from the retired `/fine-tunes` job endpoint.

## Replace a simple Assistant run

Move application instructions into each Responses request. Choose a model available to your account through `OPENAI_MODEL`:

```kotlin
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.api.response.ResponseInput
import com.aallam.openai.api.response.ResponseRequest
import com.aallam.openai.client.OpenAI

suspend fun main() {
    val token = checkNotNull(System.getenv("OPENAI_API_KEY"))
    val model = ModelId(checkNotNull(System.getenv("OPENAI_MODEL")))
    OpenAI(token).use { client ->
        val first = client.response(
            ResponseRequest(
                model = model,
                instructions = "You are a concise math tutor.",
                input = ResponseInput("Solve 3x + 11 = 14."),
            )
        )
        println(first.output.flatMap { it.content.orEmpty() }.mapNotNull { it.text }.joinToString(""))

        val next = client.response(
            ResponseRequest(
                model = model,
                previousResponseId = first.id,
                instructions = "You are a concise math tutor.",
                input = ResponseInput("Explain how to check the answer."),
            )
        )
        println(next.output.flatMap { it.content.orEmpty() }.mapNotNull { it.text }.joinToString(""))
    }
}
```

`previous_response_id` continues a stored response chain; it does not carry forward prior instructions, so send the application instructions again. If persistence is unsuitable, send the required input history explicitly and choose the appropriate storage controls. File search, code execution and other hosted tools require their own declarations and result handling; changing a URL or model name is not a complete tool migration.

## Existing code and other providers

Legacy methods remain callable. This permits gradual migration and avoids forcing unrelated source changes on users of OpenAI-compatible providers. Whether another provider supports a retained endpoint, model or extension must be verified against that provider's contract.

New operations on existing client interfaces have default unsupported implementations so custom clients can continue compiling. The built-in OpenAI client implements the supported operations. A custom client must implement a new operation before using it.
