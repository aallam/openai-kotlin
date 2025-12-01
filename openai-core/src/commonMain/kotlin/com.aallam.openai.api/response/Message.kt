package com.aallam.openai.api.response

import com.aallam.openai.api.OpenAIDsl
import com.aallam.openai.api.chat.ChatRole
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A message output item.
 */
@Serializable
@SerialName("message")
public data class Message(
    /**
     * Unique identifier for this message. Optional
     */
    @SerialName("id") public val id: String? = null,

    /**
     * The role of the message author.
     */
    @SerialName("role") public val role: ChatRole,

    /**
     * The content of the message.
     */
    @SerialName("content") public val content: List<MessageContent>,

    /**
     * The status of the message.
     */
    @SerialName("status") public val status: MessageStatus? = null,
) : ResponseInputItem, ResponseOutputItem

/**
 * Content within a message output.
 */
@Serializable
public sealed interface MessageContent {

    /**
     * Text content within a message.
     */
    @Serializable
    @SerialName("output_text")
    public data class OutputText(
        /**
         * The text content.
         */
        @SerialName("text") public val text: String,

        /**
         * Annotations for the text content.
         */
        @EncodeDefault(EncodeDefault.Mode.ALWAYS)
        @SerialName("annotations") public val annotations: List<Annotation> = emptyList(),
    ) : MessageContent

    @Serializable
    @SerialName("input_text")
    public data class InputText(
        /**
         * The text content.
         */
        @SerialName("text") public val text: String,
    ) : MessageContent

    @Serializable
    @SerialName("refusal")
    public data class Refusal(
        /**
         * The reason for the refusal.
         */
        @SerialName("refusal") public val refusal: String,
    ) : MessageContent
}

@Serializable
public enum class MessageStatus {
    @SerialName("completed")
    Completed,

    @SerialName("in_progress")
    InProgress,

    @SerialName("incomplete")
    Incomplete,
}

@Serializable
public sealed class Annotation {
    @Serializable
    @SerialName("file_citation")
    public data class FileCitation(
        @SerialName("file_id") public val fileId: String,
        @SerialName("filename") public val filename: String,
        @SerialName("index") public val index: Int,
    ) : Annotation()

    @Serializable
    @SerialName("url_citation")
    public data class UrlCitation(
        @SerialName("url") public val url: String,
        @SerialName("title") public val title: String,
        @SerialName("start_index") public val startIndex: Int,
        @SerialName("end_index") public val endIndex: Int,
    ) : Annotation()

    @Serializable
    @SerialName("container_file_citation")
    public data class ContainerFileCitation(
        @SerialName("file_id") public val fileId: String,
        @SerialName("container_id") public val containerId: String,
        @SerialName("filename") public val filename: String,
        @SerialName("start_index") public val startIndex: Int,
        @SerialName("end_index") public val endIndex: Int,
    ) : Annotation()

    @Serializable
    @SerialName("file_path")
    public data class FilePath(
        @SerialName("file_id") public val fileId: String,
        @SerialName("index") public val index: Int,
    ) : Annotation()
}

/**
 * Builder for message input items.
 */
@OpenAIDsl
public class MessageBuilder {
    private val content = mutableListOf<MessageContent>()

    public var id: String? = null

    /**
     * The role of the message author.
     */
    public var role: ChatRole? = null

    public var status: MessageStatus? = null

    /**
     * The content of the message.
     */
    public fun content(block: MessageContentBuilder.() -> Unit) {
        this.content.addAll(MessageContentBuilder().apply(block).build())
    }

    internal fun build(): Message = Message(
        id = id,
        role = requireNotNull(role) { "role is required" },
        content = requireNotNull(content) { "content is required" },
        status = status
    )
}

@OpenAIDsl
public class MessageContentBuilder {
    private val items = mutableListOf<MessageContent>()

    public fun outputText(block: OutputTextBuilder.() -> Unit) {
        items.add(OutputTextBuilder().apply(block).build())
    }

    public fun outputText(text: String) {
        items.add(MessageContent.OutputText(text))
    }

    public fun inputText(text: String) {
        items.add(MessageContent.InputText(text))
    }

    internal fun build(): List<MessageContent> = items.toList()
}

@OpenAIDsl
public class OutputTextBuilder {
    private val annotations = mutableListOf<Annotation>()
    public var text: String? = null

    public fun annotation(block: AnnotationsBuilder.() -> Unit) {
        annotations.addAll(AnnotationsBuilder().apply(block).build())
    }

    internal fun build(): MessageContent.OutputText = MessageContent.OutputText(
        text = requireNotNull(text) { "text is required" },
        annotations = annotations.toList()
    )
}

@OpenAIDsl
public class AnnotationsBuilder {
    private val items = mutableListOf<Annotation>()

    public fun fileCitation(block: FileCitationBuilder.() -> Unit) {
        items.add(FileCitationBuilder().apply(block).build())
    }

    internal fun build(): List<Annotation> = items.toList()
}

@OpenAIDsl
public class FileCitationBuilder {
    public var fileId: String? = null
    public var filename: String? = null
    public var index: Int? = null

    internal fun build(): Annotation = Annotation.FileCitation(
        fileId = requireNotNull(fileId) { "fileId is required" },
        filename = requireNotNull(filename) { "filename is required" },
        index = requireNotNull(index) { "index is required" },
    )
}

@OpenAIDsl
public class UrlCitationBuilder {
    public var url: String? = null
    public var title: String? = null
    public var startIndex: Int? = null
    public var endIndex: Int? = null

    internal fun build(): Annotation = Annotation.UrlCitation(
        url = requireNotNull(url) { "url is required" },
        title = requireNotNull(title) { "title is required" },
        startIndex = requireNotNull(startIndex) { "startIndex is required" },
        endIndex = requireNotNull(endIndex) { "endIndex is required" },
    )
}

@OpenAIDsl
public class ContainerFileCitationBuilder {
    public var fileId: String? = null
    public var containerId: String? = null
    public var filename: String? = null
    public var startIndex: Int? = null
    public var endIndex: Int? = null

    internal fun build(): Annotation = Annotation.ContainerFileCitation(
        fileId = requireNotNull(fileId) { "fileId is required" },
        containerId = requireNotNull(containerId) { "containerId is required" },
        filename = requireNotNull(filename) { "filename is required" },
        startIndex = requireNotNull(startIndex) { "startIndex is required" },
        endIndex = requireNotNull(endIndex) { "endIndex is required" },
    )
}

@OpenAIDsl
public class FilePathBuilder {
    public var fileId: String? = null
    public var index: Int? = null

    internal fun build(): Annotation = Annotation.FilePath(
        fileId = requireNotNull(fileId) { "fileId is required" },
        index = requireNotNull(index) { "index is required" },
    )
}

