package com.aallam.openai.api.chat

import com.aallam.openai.api.core.Role
import kotlinx.serialization.*
import kotlinx.serialization.json.*

/** Typed content for advanced Chat inputs. */
@Serializable(with = ChatInputContentSerializer::class)
public sealed interface ChatInputContent

/** Plain text input. */
@Serializable
@kotlin.jvm.JvmInline
public value class ChatInputText(public val value: String) : ChatInputContent

/** Multimodal content parts. */
@Serializable
@kotlin.jvm.JvmInline
public value class ChatInputParts(public val value: List<ChatInputPart>) : ChatInputContent

internal object ChatInputContentSerializer : JsonContentPolymorphicSerializer<ChatInputContent>(ChatInputContent::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<ChatInputContent> =
        if (element is JsonArray) ChatInputParts.serializer() else ChatInputText.serializer()
}

/** Independent multimodal input part hierarchy. */
@Serializable
public sealed interface ChatInputPart {
    /** Text input and optional cache boundary. */
    @Serializable @SerialName("text")
    public data class Text(
        /** Input text. */
        public val text: String,
        /** Optional cache boundary. */
        @SerialName("prompt_cache_breakpoint") public val promptCacheBreakpoint: PromptCacheBreakpoint? = null,
    ) : ChatInputPart

    /** Image input and optional cache boundary. */
    @Serializable @SerialName("image_url")
    public data class Image(
        /** Image URL and detail. */
        @SerialName("image_url") public val imageUrl: ImagePart.ImageURL,
        /** Optional cache boundary. */
        @SerialName("prompt_cache_breakpoint") public val promptCacheBreakpoint: PromptCacheBreakpoint? = null,
    ) : ChatInputPart

    /** Base64-encoded audio input. */
    @Serializable @SerialName("input_audio")
    public data class Audio(
        /** Audio data and encoding. */
        @SerialName("input_audio") public val inputAudio: ChatInputAudio,
        /** Optional explicit cache boundary after this part. */
        @SerialName("prompt_cache_breakpoint") public val promptCacheBreakpoint: PromptCacheBreakpoint? = null,
    ) : ChatInputPart

    /** Uploaded or inline file input. */
    @Serializable @SerialName("file")
    public data class File(
        /** File reference or inline bytes. */
        public val file: ChatInputFile,
        /** Optional explicit cache boundary after this part. */
        @SerialName("prompt_cache_breakpoint") public val promptCacheBreakpoint: PromptCacheBreakpoint? = null,
    ) : ChatInputPart
}

/** Encoded audio input. */
@Serializable
public data class ChatInputAudio(
    /** Base64-encoded audio. */
    public val data: String,
    /** Input audio format, such as wav or mp3. */
    public val format: String,
)

/** Uploaded file or inline file data. */
@Serializable
public data class ChatInputFile(
    /** Uploaded file ID. */
    @SerialName("file_id") public val fileId: String? = null,
    /** Base64-encoded file contents. */
    @SerialName("file_data") public val fileData: String? = null,
    /** Inline file name. */
    public val filename: String? = null,
)

/** Advanced input message supporting multimodal content and custom tool calls. */
@Serializable
public data class ChatInputMessage(
    /** Message author. */
    public val role: Role,
    /** Plain text or structured content. */
    public val content: ChatInputContent? = null,
    /** Optional participant name. */
    public val name: String? = null,
    /** Calls emitted by an assistant message. */
    @SerialName("tool_calls") public val toolCalls: List<ChatResultToolCall>? = null,
    /** Call answered by a tool message. */
    @SerialName("tool_call_id") public val toolCallId: String? = null,
    /** Prior audio output reference. */
    public val audio: ChatAudio? = null,
    /** Refusal text for assistant history. */
    public val refusal: String? = null,
)
