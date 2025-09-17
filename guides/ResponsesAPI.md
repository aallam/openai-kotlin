# Responses API Guide

The Responses API is OpenAI's stateless API that provides access to reasoning traces from reasoning models like o1, o3, and others. This guide shows how to use the Responses API in the OpenAI Kotlin SDK.

## Key Features

- **Stateless operation**: No server-side state management (store=false always)
- **Reasoning access**: Get detailed reasoning traces from reasoning models
- **Manual context management**: Full control over conversation history
- **Type-safe**: Full Kotlin type safety for all interactions

## Basic Usage

### Simple Response

```kotlin
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.api.response.*
import com.aallam.openai.client.OpenAI

val openAI = OpenAI(token = "your-api-key")

val response = openAI.createResponse(
    responseRequest {
        model = ModelId("gpt-4o")
        input {
            message(ChatRole.User, "Hello, how are you?")
        }
    }
)

println("Response: ${response.firstMessageText}")
```

### Accessing Reasoning Traces

To get reasoning traces from reasoning models, include "reasoning.encrypted_content" in your request:

```kotlin
val response = openAI.createResponse(
    responseRequest {
        model = ModelId("gpt-5") // Use a reasoning model
        reasoning = ReasoningConfig(effort = "medium", summary = "detailed")
        include = listOf("reasoning.encrypted_content") // Request reasoning traces
        input {
            message(ChatRole.User, "Solve this step by step: What is the square root of 144?")
        }
    }
)

// Access the encrypted reasoning content from output items
val reasoningOutput = response.output.filterIsInstance<ResponseOutputItem.Reasoning>().firstOrNull()
val encryptedContent = reasoningOutput?.encryptedContent
val reasoningSummary = reasoningOutput?.summary

println("Encrypted reasoning: $encryptedContent")
println("Reasoning summary: $reasoningSummary")

// Access the final answer
println("Answer: ${response.firstMessageText}")
```

### Multi-turn Conversations with Reasoning

Since the API is stateless, you need to manually manage conversation history and pass previous reasoning traces:

```kotlin
// First interaction
val firstResponse = openAI.createResponse(
    responseRequest {
        model = ModelId("gpt-5")
        reasoning = ReasoningConfig(effort = "medium", summary = "detailed")
        include = listOf("reasoning.encrypted_content")
        input {
            message(ChatRole.User, "What is 15 * 23?")
        }
    }
)

// Extract encrypted reasoning content from the first response
val firstReasoningOutput = firstResponse.output.filterIsInstance<ResponseOutputItem.Reasoning>().firstOrNull()
val firstEncryptedContent = firstReasoningOutput?.encryptedContent
val firstAnswer = firstResponse.firstMessageText

// Second interaction - include previous context and reasoning
val secondResponse = openAI.createResponse(
    responseRequest {
        model = ModelId("gpt-5")
        reasoning = ReasoningConfig(effort = "medium", summary = "detailed")
        include = listOf("reasoning.encrypted_content")
        input {
            // Previous conversation
            message(ChatRole.User, "What is 15 * 23?")
            message(ChatRole.Assistant, firstAnswer ?: "345")

            // Previous reasoning (if available)
            if (firstEncryptedContent != null) {
                reasoning {
                    content = listOf(ReasoningEncryptedPart(firstEncryptedContent))
                    summary = listOf(SummaryTextPart("Previous calculation reasoning"))
                    encryptedContent = firstEncryptedContent
                }
            }

            // New question
            message(ChatRole.User, "Now divide that result by 5")
        }
    }
)

println("Second answer: ${secondResponse.firstMessageText}")

// Access second reasoning
val secondReasoningOutput = secondResponse.output.filterIsInstance<ResponseOutputItem.Reasoning>().firstOrNull()
println("Second reasoning summary: ${secondReasoningOutput?.summary}")
```

## Configuration Options

### Reasoning Configuration

```kotlin
reasoning = ReasoningConfig(
    effort = "high", // "low", "medium", or "high"
    summary = "detailed" // "auto", "concise", or "detailed"
)
```

### Request Parameters

```kotlin
responseRequest {
    model = ModelId("gpt-5")
    temperature = 0.7
    maxOutputTokens = 1000
    instructions = "Please provide step-by-step reasoning"
    topP = 0.9
    reasoning = ReasoningConfig(effort = "medium", summary = "detailed")
    include = listOf("reasoning.encrypted_content")
    // ... input
}
```



## Best Practices

1. **Use reasoning models** (gpt-5, o1, o3, etc.) when you want reasoning traces
2. **Include "reasoning.encrypted_content"** in the include parameter to get reasoning traces
3. **Set summary parameter** to "detailed" for comprehensive reasoning summaries
4. **Manage context manually** by passing previous messages and encrypted reasoning content
5. **Store encrypted content** from `ResponseOutputItem.Reasoning` for future interactions
6. **Use appropriate effort levels** - higher effort may provide more detailed reasoning but takes longer

## Error Handling

```kotlin
try {
    val response = openAI.createResponse(request)
    if (response.error != null) {
        println("Error: ${response.error?.message}")
    } else {
        println("Success: ${response.firstMessageText}")

        // Check for reasoning content
        val reasoningOutput = response.output.filterIsInstance<ResponseOutputItem.Reasoning>().firstOrNull()
        if (reasoningOutput?.encryptedContent != null) {
            println("Reasoning trace available")
        }
    }
} catch (e: Exception) {
    println("Request failed: ${e.message}")
}
```

## Model Support

The Responses API works with various OpenAI models:

- **Reasoning models**: gpt-5, o1, o3, o1-mini (provide reasoning traces and summaries)
- **Standard models**: gpt-4o, gpt-4o-mini (limited reasoning support)
- **Specialized models**: computer-use-preview, gpt-image-1

For full reasoning traces and summaries, use reasoning models like gpt-5, o1, or o3.

## Current Limitations

This implementation provides a subset of the full OpenAI Responses API functionality:

1. **Stateless only**: Currently only supports stateless mode (store=false). The full API supports stateful conversation management
2. **Manual context management**: You must manually pass all previous messages and reasoning traces. The full API can automatically manage conversation context
3. **Limited reasoning access**: Access to reasoning content is through encrypted traces only. The full API may provide additional reasoning formats
4. **Basic output structure**: Uses simplified `ResponseOutputItem` structure. The full API may have additional output types and metadata
