package com.aallam.openai.api.message

import com.aallam.openai.api.file.FileId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * The content of the message as text or image.
 */
@Serializable
public sealed interface MessageContent {

    /**
     * The content of the message as text.
     */
    @Serializable
    @SerialName("text")
    public data class Text(@SerialName("text") val text: TextContent) : MessageContent

    /**
     * References an image File in the content of a message.
     */
    @Serializable
    @SerialName("image_file")
    public data class Image(
        /**
         * The File ID of the image in the message content.
         */
        @SerialName("file_id") val fileId: FileId
    ) : MessageContent

}

/**
 * The text content of the message value and annotations.
 */
@Serializable
public data class TextContent(
    /**
     * The data that makes up the text.
     */
    @SerialName("value") val value: String,
    /**
     * Annotations for the text.
     */
    @SerialName("annotations") val annotations: List<TextAnnotation>
)

/**
 * Annotations for a text.
 */
@Serializable
public sealed interface TextAnnotation

@Serializable
public data class FileCitation(
    /**
     * The ID of the specific File the citation is from.
     */
    @SerialName("file") val fileId: FileId,

    /**
     * The specific quote in the file.
     */
    @SerialName("quote") val quote: String,
)

/**
 * A URL for the file that's generated when the assistant used the code interpreter tool to generate a file.
 */
@Serializable
@SerialName("file_path")
public data class FilePathAnnotation(
    /**
     * The text in the message content that needs to be replaced.
     */
    @SerialName("text") val text: String,

    /**
     * The file path.
     */
    @SerialName("file_path") val filePath: FilePath,

    /**
     * Start index.
     */
    @SerialName("start_index") val startIndex: Int,

    /**
     * End index.

     */
    @SerialName("end_index") val endIndex: Int
) : TextAnnotation

@Serializable
public data class FilePath(
    /**
     * The ID of the file that was generated.
     */
    @SerialName("path") val path: String
)
