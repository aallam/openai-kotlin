package com.aallam.openai.api.misc

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.*
import com.aallam.openai.api.core.Role
import com.aallam.openai.api.model.ModelId
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import kotlin.test.*

@OptIn(BetaOpenAI::class)
class TestChatParity {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun builderForwardsRequestControlsAndSeed() {
        val request = chatCompletionRequest {
            model = ModelId("model")
            messages = listOf(ChatMessage(Role.Developer, "instructions"))
            seed = 123
        }
        val options = ChatCompletionOptions(
            metadata = mapOf("tenant" to "example"),
            parallelToolCalls = false,
            serviceTier = "flex",
            safetyIdentifier = "user-hash",
            promptCacheKey = "prefix",
            promptCacheOptions = ChatPromptCacheOptions("explicit", "30m"),
            verbosity = "low",
            moderation = ChatModeration("omni-moderation-latest", ChatModerationPolicy(input = ChatModerationMode("block"))),
            modalities = listOf("text", "audio"),
            audio = ChatAudioConfig("pcm16", "alloy"),
            prediction = ChatPrediction("expected"),
            streamOptions = ChatStreamOptions(includeUsage = true, includeObfuscation = false),
            tools = listOf(ChatFunctionTool(ChatFunctionDefinition("lookup", strict = true))),
        )
        val wire = json.encodeToJsonElement(options).jsonObject
        assertEquals(123, json.encodeToJsonElement(request).jsonObject["seed"]?.jsonPrimitive?.int)
        assertEquals(true, wire["tools"]!!.jsonArray.single().jsonObject["function"]?.jsonObject?.get("strict")?.jsonPrimitive?.boolean)
        assertEquals(false, wire["parallel_tool_calls"]?.jsonPrimitive?.boolean)
        assertEquals("flex", wire["service_tier"]?.jsonPrimitive?.content)
        assertEquals("example", wire["metadata"]?.jsonObject?.get("tenant")?.jsonPrimitive?.content)
        assertEquals("explicit", wire["prompt_cache_options"]?.jsonObject?.get("mode")?.jsonPrimitive?.content)
        assertEquals("block", wire["moderation"]?.jsonObject?.get("policy")?.jsonObject?.get("input")?.jsonObject?.get("mode")?.jsonPrimitive?.content)
        assertEquals("expected", wire["prediction"]?.jsonObject?.get("content")?.jsonPrimitive?.content)
        assertEquals(false, wire["stream_options"]?.jsonObject?.get("include_obfuscation")?.jsonPrimitive?.boolean)
        assertEquals(request, json.decodeFromString<ChatCompletionRequest>(json.encodeToString(request)))
    }

    @Test
    fun advancedOptionsSupportMultimodalInputsAndCustomResults() {
        val options = ChatCompletionOptions(
            messages = listOf(ChatInputMessage(Role.User, ChatInputParts(listOf(
                ChatInputPart.Text("cached", PromptCacheBreakpoint(ttl = "30m")),
                ChatInputPart.Audio(ChatInputAudio("YXVkaW8=", "wav"), PromptCacheBreakpoint(ttl = "30m")),
                ChatInputPart.File(ChatInputFile(fileId = "file_123"), PromptCacheBreakpoint(ttl = "12h")),
            )))),
            tools = listOf(CustomTool(CustomToolDefinition("calculate", format = CustomToolFormat("grammar", CustomToolGrammar("[0-9]+", "regex"))))),
            toolChoice = ChatToolSelection("custom", custom = FunctionToolChoice("calculate")),
        )
        val wire = json.encodeToJsonElement(options).jsonObject
        val parts = wire["messages"]!!.jsonArray[0].jsonObject["content"]!!.jsonArray
        assertEquals("30m", parts[1].jsonObject["prompt_cache_breakpoint"]?.jsonObject?.get("ttl")?.jsonPrimitive?.content)
        assertEquals("12h", parts[2].jsonObject["prompt_cache_breakpoint"]?.jsonObject?.get("ttl")?.jsonPrimitive?.content)
        assertEquals("input_audio", parts[1].jsonObject["type"]?.jsonPrimitive?.content)
        assertEquals("file_123", parts[2].jsonObject["file"]?.jsonObject?.get("file_id")?.jsonPrimitive?.content)
        assertEquals("custom", wire["tools"]!!.jsonArray[0].jsonObject["type"]?.jsonPrimitive?.content)
        assertEquals(options, json.decodeFromString<ChatCompletionOptions>(json.encodeToString(options)))
        val response = json.decodeFromString<ChatCompletionResult>("""{"id":"chat_1","created":1,"model":"model","choices":[{"index":0,"message":{"role":"assistant","tool_calls":[{"type":"custom","id":"call_1","custom":{"name":"calculate","input":"42"}}]}}]}""")
        assertEquals("42", response.choices.single().message?.toolCalls?.single()?.custom?.input)
    }

    @Test
    fun preservesNestedChatCitationsAndDetailedUsage() {
        val completion = json.decodeFromString<ChatCompletionResult>("""{
          "id":"chat_1","created":1,"model":"model","service_tier":"flex","metadata":{"tag":"x"},
          "choices":[{"index":0,"finish_reason":"stop","message":{"role":"assistant","content":"source","audio":{"id":"audio_1","data":"YQ==","transcript":"a","expires_at":10},"annotations":[{"type":"url_citation","url_citation":{"url":"https://example.com","title":"Example","start_index":0,"end_index":6}}]}}],
          "usage":{"prompt_tokens_details":{"cached_tokens":12,"audio_tokens":3},"completion_tokens_details":{"reasoning_tokens":4,"audio_tokens":5,"accepted_prediction_tokens":6,"rejected_prediction_tokens":7}}
        }""")
        assertEquals("https://example.com", completion.choices.single().message?.annotations?.single()?.urlCitation?.url)
        assertEquals("a", completion.choices.single().message?.audio?.transcript)
        assertEquals(12, completion.usage?.promptTokensDetails?.cachedTokens)
        assertEquals(7, completion.usage?.completionTokensDetails?.rejectedPredictionTokens)
        assertEquals("flex", completion.serviceTier)
        val flat = json.decodeFromString<ChatAnnotation>("""{"type":"url_citation","url":"https://example.com","title":"Flat"}""")
        assertEquals("Flat", flat.title)
        val nested = Json.decodeFromString<ChatAnnotation>("""{"type":"url_citation","url_citation":{"url":"https://example.com","title":"Nested","start_index":1,"end_index":5}}""")
        assertEquals("Nested", nested.title)
        assertEquals("https://example.com", nested.url)
        assertEquals(1, nested.startIndex)
        assertEquals(5, nested.endIndex)
        assertEquals(nested, Json.decodeFromString<ChatAnnotation>(Json.encodeToString(nested)))

    }
}
