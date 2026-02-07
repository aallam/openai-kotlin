# Assistants

This guide provides a step-by-step example for using the Assistants API from Kotlin.

> ℹ️ Assistants/Threads/Messages/Runs APIs in this client are marked as beta.

## Prerequisites

- OpenAI API Kotlin client installed.
- An API key from OpenAI.

## Complete Example

```kotlin
suspend fun main() {

    // 1) Configure client
    val token = System.getenv("OPENAI_API_KEY")
    val openAI = OpenAI(token)

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
