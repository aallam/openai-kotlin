# Assistants

> **Sunset:** OpenAI sunset the Assistants API on August 26, 2026 ([#470](https://github.com/aallam/openai-kotlin/issues/470)). The client retains these beta endpoints for compatible services that still implement them, but they are not a supported path for OpenAI. For new OpenAI integrations, use the [Responses API](GettingStarted.md#responses).

This guide documents the legacy Assistants API methods for compatible services.

> ℹ️ Assistants/Threads/Messages/Runs APIs in this client are marked as beta.

## Migrating to Responses

The Responses API is available in this client. Move assistant instructions into each `ResponseRequest`, provide turn input with `ResponseInput`, and continue a conversation with `previousResponseId`. This is not a one-to-one migration: Responses do not create a stored assistant, thread, or run, and this client currently exposes non-streaming Responses methods.

```kotlin
val first = openAI.response(
    request = ResponseRequest(
        model = ModelId("gpt-4.1"),
        instructions = "You are a math tutor.",
        input = ResponseInput("What is 3 + 11?")
    )
)

val followUp = openAI.response(
    request = ResponseRequest(
        model = ModelId("gpt-4.1"),
        previousResponseId = first.id,
        input = ResponseInput("Can you show another way?")
    )
)
```

See [Getting Started](GettingStarted.md#responses) for the full Responses API surface.

## Prerequisites

- OpenAI API Kotlin client installed.
- A compatible service that still implements the Assistants API, with its API base URL (including `/v1/` and ending in `/`) and token. Do not use an OpenAI API key with these legacy examples.

## Complete Example

```kotlin
suspend fun main() {

    // 1) Configure a compatible host that still supports Assistants
    val baseUrl = System.getenv("ASSISTANTS_API_BASE_URL")
        ?: error("ASSISTANTS_API_BASE_URL is required")
    val token = System.getenv("ASSISTANTS_API_TOKEN")
        ?: error("ASSISTANTS_API_TOKEN is required")
    val openAI = OpenAI(
        OpenAIConfig(
            token = token,
            host = OpenAIHost(baseUrl = baseUrl),
        )
    )

    // 2) Create an assistant
    val assistant = openAI.assistant(
        request = AssistantRequest(
            name = "Math Tutor",
            instructions = "You are a personal math tutor. Write and run code to answer math questions.",
            tools = listOf(AssistantTool.CodeInterpreter),
            model = ModelId("gpt-4o-mini")
        )
    )

    // 3) Create a thread
    val thread = openAI.thread()

    // 4) Add a user message to the thread
    openAI.message(
        threadId = thread.id,
        request = MessageRequest(
            role = Role.User,
            content = "I need to solve the equation 3x + 11 = 14. Can you help me?"
        )
    )

    // 5) Start a run
    val run = openAI.createRun(
        threadId = thread.id,
        request = RunRequest(
            assistantId = assistant.id,
            instructions = "Please address the user as Jane Doe."
        )
    )

    // 6) Poll until completed
    var retrievedRun: Run
    do {
        delay(1500)
        retrievedRun = openAI.getRun(threadId = thread.id, runId = run.id)
    } while (retrievedRun.status != Status.Completed)

    // 7) Read assistant messages
    val messages = openAI.messages(thread.id)
    println("Assistant response:")
    for (message in messages) {
        val text = message.content.firstOrNull() as? MessageContent.Text ?: continue
        println(text.text.value)
    }
}
```

## Notes

- Each end user should generally get their own thread.
- If a run moves to `Status.RequiresAction`, submit tool outputs with `openAI.submitToolOutput(...)`.
- For streaming run events, use `openAI.createStreamingRun(...)` or `openAI.createStreamingThreadRun(...)`.
