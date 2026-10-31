@file:OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)

package com.aallam.openai.api.decision

import kotlinx.serialization.*
import kotlinx.serialization.json.*
import kotlin.jvm.JvmInline

/** Shared plain text or ordered conversation evidence for Decisions. */
@Serializable(with = DecisionInputSerializer::class)
public sealed interface DecisionInput {
    /** Plain text evidence. */
    @Serializable @JvmInline
    public value class Text(public val value: String) : DecisionInput
    /** Ordered messages, tool calls and matching tool outputs. */
    @Serializable @JvmInline
    public value class Items(public val value: List<DecisionInputItem>) : DecisionInput
}

/** A string or a list of typed parts, interpreted according to its message or question context. */
@Serializable(with = DecisionContentSerializer::class)
public sealed interface DecisionContent {
    /** Plain text content. */
    @Serializable @JvmInline
    public value class Text(public val value: String) : DecisionContent
    /** Ordered content parts; each enclosing context restricts the allowed part kinds. */
    @Serializable @JvmInline
    public value class Parts(public val value: List<DecisionContentPart>) : DecisionContent
}

/** A typed content part in shared evidence, question instructions or assistant history. */
@Serializable
public sealed interface DecisionContentPart {
    /** Input text, optionally ending a reusable cache prefix in shared evidence. */
    @Serializable @SerialName("input_text")
    public data class InputText(
        /** Text evidence or question instructions. */
        public val text: String,
        /** Explicit cache boundary; not supported in question instructions. */
        @SerialName("prompt_cache_breakpoint") public val promptCacheBreakpoint: DecisionPromptCacheBreakpoint? = null,
    ) : DecisionContentPart

    /** Image evidence supplied by a data URL or public HTTP(S) URL, never a file ID. */
    @Serializable @SerialName("input_image")
    public data class InputImage(
        /** Public HTTP(S) URL or base64 data URL. */
        @SerialName("image_url") public val imageUrl: String,
        /** Model image detail level, such as `low`, `high`, `auto` or `original`. */
        public val detail: String? = null,
        /** Explicit cache boundary; not supported in question instructions. */
        @SerialName("prompt_cache_breakpoint") public val promptCacheBreakpoint: DecisionPromptCacheBreakpoint? = null,
    ) : DecisionContentPart

    /** Structured evidence. Only JSON objects or arrays of objects are accepted. */
    @Serializable @SerialName("input_json")
    public data class InputJson(
        /** Object or object array; nested values may be any JSON value. */
        public val data: JsonElement,
    ) : DecisionContentPart {
        init {
            require(data is JsonObject || data is JsonArray && data.all { it is JsonObject }) {
                "Decision JSON evidence must be an object or array of objects"
            }
        }
    }

    /** Text from an earlier assistant response. */
    @Serializable @SerialName("output_text")
    public data class OutputText(
        /** Assistant text. */
        public val text: String,
    ) : DecisionContentPart

    /** Refusal from an earlier assistant response. */
    @Serializable @SerialName("refusal")
    public data class Refusal(
        /** Assistant refusal text. */
        public val refusal: String,
    ) : DecisionContentPart
}

/** An explicit boundary at the exact end of a reusable shared-evidence prefix. */
@Serializable
public data class DecisionPromptCacheBreakpoint(
    /** The API supports only `explicit` for a content breakpoint. */
    @EncodeDefault public val mode: String = "explicit",
) {
    init { require(mode == "explicit") { "Decision cache breakpoints must be explicit" } }
}

/** A message, tool call or matching output in Decisions conversation history. */
@Serializable
public sealed interface DecisionInputItem {
    /** Conversation message; content kinds are validated for the chosen role. */
    @Serializable @SerialName("message")
    public data class Message(
        /** Text or role-appropriate parts. */
        public val content: DecisionContent,
        /** User supports text/images/JSON; developer/system support text; assistant supports output/refusal. */
        @EncodeDefault public val role: DecisionRole = DecisionRole.User,
        /** Optional assistant phase, such as `commentary` or `final_answer`. */
        public val phase: String? = null,
    ) : DecisionInputItem {
        init {
            require(phase == null || role == DecisionRole.Assistant) { "Only assistant messages have a phase" }
            content.requireParts("message role $role") { part ->
                when (role) {
                    DecisionRole.User -> part.isInput()
                    DecisionRole.Developer, DecisionRole.System -> part is DecisionContentPart.InputText
                    DecisionRole.Assistant -> part is DecisionContentPart.OutputText || part is DecisionContentPart.Refusal
                }
            }
        }
    }

    /** Earlier function call; supply a matching output with this call ID. */
    @Serializable @SerialName("function_call")
    public data class FunctionCall(
        /** Function arguments as serialized JSON text. */
        public val arguments: String,
        /** Identifier linking this call to its output. */
        @SerialName("call_id") public val callId: String,
        /** Function name. */
        public val name: String,
        /** Optional item identifier. */
        public val id: String? = null,
        /** Optional tool namespace. */
        public val namespace: String? = null,
        /** Optional `in_progress`, `completed` or `incomplete` status. */
        public val status: String? = null,
    ) : DecisionInputItem

    /** Function output used as evidence, containing text or input text/image parts. */
    @Serializable @SerialName("function_call_output")
    public data class FunctionCallOutput(
        /** Identifier of the matching function call. */
        @SerialName("call_id") public val callId: String,
        /** Text or text/image parts returned by the tool. */
        public val output: DecisionContent,
        /** Optional item identifier. */
        public val id: String? = null,
        /** Optional `in_progress`, `completed` or `incomplete` status. */
        public val status: String? = null,
    ) : DecisionInputItem {
        init { output.requireToolOutput() }
    }

    /** Earlier custom tool call; supply a matching output with this call ID. */
    @Serializable @SerialName("custom_tool_call")
    public data class CustomToolCall(
        /** Identifier linking this call to its output. */
        @SerialName("call_id") public val callId: String,
        /** Unstructured tool input. */
        public val input: String,
        /** Tool name. */
        public val name: String,
        /** Optional item identifier. */
        public val id: String? = null,
        /** Optional tool namespace. */
        public val namespace: String? = null,
        /** Optional `in_progress`, `completed` or `incomplete` status. */
        public val status: String? = null,
    ) : DecisionInputItem

    /** Custom tool output containing text or input text/image parts. */
    @Serializable @SerialName("custom_tool_call_output")
    public data class CustomToolCallOutput(
        /** Identifier of the matching custom tool call. */
        @SerialName("call_id") public val callId: String,
        /** Text or text/image parts returned by the tool. */
        public val output: DecisionContent,
        /** Optional item identifier. */
        public val id: String? = null,
        /** Optional `in_progress`, `completed` or `incomplete` status. */
        public val status: String? = null,
    ) : DecisionInputItem {
        init { output.requireToolOutput() }
    }
}

/** Supported conversation message roles. */
@Serializable
public enum class DecisionRole {
    /** User evidence: text, images or JSON. */
    @SerialName("user") User,
    /** Developer instructions containing text. */
    @SerialName("developer") Developer,
    /** System instructions, treated as developer instructions by the API. */
    @SerialName("system") System,
    /** Earlier assistant output or refusal. */
    @SerialName("assistant") Assistant,
}

internal fun DecisionContentPart.isInput(): Boolean = this is DecisionContentPart.InputText ||
    this is DecisionContentPart.InputImage || this is DecisionContentPart.InputJson

internal fun DecisionContent.requireParts(context: String, allowed: (DecisionContentPart) -> Boolean) {
    if (this is DecisionContent.Parts) require(value.all(allowed)) { "Unsupported Decisions content part for $context" }
}

private fun DecisionContent.requireToolOutput() = requireParts("tool output") {
    it is DecisionContentPart.InputText || it is DecisionContentPart.InputImage
}

internal object DecisionInputSerializer : JsonContentPolymorphicSerializer<DecisionInput>(DecisionInput::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<DecisionInput> = when {
        element is JsonPrimitive && element.isString -> DecisionInput.Text.serializer()
        element is JsonArray -> DecisionItemsSerializer
        else -> throw SerializationException("Decision input must be a string or array of items")
    }
}

private object DecisionItemsSerializer : JsonTransformingSerializer<DecisionInput.Items>(DecisionInput.Items.serializer()) {
    override fun transformDeserialize(element: JsonElement): JsonElement = JsonArray(element.jsonArray.map { item ->
        if (item is JsonObject && "type" !in item && "content" in item) {
            JsonObject(item + ("type" to JsonPrimitive("message")))
        } else item
    })
}

internal object DecisionContentSerializer : JsonContentPolymorphicSerializer<DecisionContent>(DecisionContent::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<DecisionContent> = when {
        element is JsonPrimitive && element.isString -> DecisionContent.Text.serializer()
        element is JsonArray -> DecisionContent.Parts.serializer()
        else -> throw SerializationException("Decision content must be a string or array of parts")
    }
}
