# Assistants

This guide provides step-by-step instructions on how to integrate OpenAI's Assistants API in a Kotlin application.

## Prerequisites

- OpenAI API Client for Kotlin installed.
- An API key from OpenAI.

## Steps to Follow

### Step 1: Setting Up the Environment

Ensure that you have the necessary OpenAI SDK and dependencies in your Kotlin project. 
Set the `OPENAI_API_KEY` environment variable with your API key.

```kotlin
val token = System.getenv("OPENAI_API_KEY")
val openAI = OpenAI(token)
```

### Step 2: Create an Assistant

```kotlin
val assistant = openAI.assistant(
    request = AssistantRequest(
        name = "Math Tutor",
        instructions = "You are a personal math tutor. Write and run code to answer math questions.",
        tools = listOf(AssistantTool.CodeInterpreter),
        model = ModelId("gpt-4-1106-preview")
    )
)
```

This code snippet creates an assistant named "Math Tutor" with instructions and tools specified.

### Step 3: Create a Thread

```kotlin
val thread = openAI.thread()
```

Create a thread to start a conversation. Each user should have their own thread.

### Step 4: Add a Message to the Thread

```kotlin
openAI.message(
    threadId = thread.id,
    request = MessageRequest(
        role = Role.User,
        content = "I need to solve the equation `3x + 11 = 14`. Can you help me?"
    )
)
```

Add a message to the thread. In this example, a math problem is posed.

### Step 5: Run the Assistant

```kotlin
val run = openAI.createRun(
    thread.id,
    request = RunRequest(
        assistantId = assistant.id,
        instructions = "Please address the user as Jane Doe. The user has a premium account.",
    )
)
```

Create a run to process the user's message and generate a response.


### Step 6: Check Run Status and Display Response

```kotlin
do {
    delay(1500)
    val retrievedRun = openAI.getRun(threadId = thread.id, runId = run.id)
} while (retrievedRun.status != Status.Completed)

val assistantMessages = openAI.messages(thread.id)
println("\nThe assistant's response:")
for (message in assistantMessages) {
    val textContent = message.content.first() as? MessageContent.Text ?: error("Expected MessageContent.Text")
    println(textContent.text.value)
}
```

Check the status of the run until it is completed, then display the assistant's response.

## Conclusion

This guide covers the basics of setting up and using the OpenAI Assistants API in a Kotlin project. 
Remember to explore additional features and capabilities of the API for more advanced use cases.

### Complete Example

Below is a complete Kotlin example following the guide:

```kotlin
suspend fun main() {

    // 1. Setup client
    val token = System.getenv("OPENAI_API_KEY")
    val openAI = OpenAI(token)
    
    // 2. Create an Assistant
    val assistant = openAI.assistant(
        request = AssistantRequest(
            name = "Math Tutor",
            instructions = "You are a personal math tutor. Write and run code to answer math questions.",
            tools = listOf(AssistantTool.CodeInterpreter),
            model = ModelId("gpt-4-1106-preview")
        )
    )

    // 3. Create a thread
    val thread = openAI.thread()

    // 4. Add a message to the thread
    openAI.message(
        threadId = thread.id,
        request = MessageRequest(
            role = Role.User,
            content = "I need to solve the equation `3x + 11 = 14`. Can you help me?"
        )
    )
    val messages = openAI.messages(thread.id)
    println("List of messages in the thread:")
    for (message in messages) {
        val textContent = message.content.first() as? MessageContent.Text ?: error("Expected MessageContent.Text")
        println(textContent.text.value)
    }

    // 5. Run the assistant
    val run = openAI.createRun(
        thread.id,
        request = RunRequest(
            assistantId = assistant.id,
            instructions = "Please address the user as Jane Doe. The user has a premium account.",
        )
    )

    // 6. Check the run status
    do {
        delay(1500)
        val retrievedRun = openAI.getRun(threadId = thread.id, runId = run.id)
    } while (retrievedRun.status != Status.Completed)

    // 6. Display the assistant's response
    val assistantMessages = openAI.messages(thread.id)
    println("\nThe assistant's response:")
    for (message in assistantMessages) {
        val textContent = message.content.first() as? MessageContent.Text ?: error("Expected MessageContent.Text")
        println(textContent.text.value)
    }
}
```
