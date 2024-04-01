package com.aallam.openai.api.message

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.file.FileId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * The content of the message as text or image.
 */
@BetaOpenAI
@Serializable
public sealed interface MessageContent {

    /**
     * The content of the message as text.
     */
    @BetaOpenAI
    @Serializable
    @SerialName("text")
    public data class Text(
        /**
         * The text content of the message value and annotations.
         */
        @SerialName("text") val text: TextContent
    ) : MessageContent

    /**
     * References an image File in the content of a message.
     */
    @BetaOpenAI
    @Serializable
    @SerialName("image_file")
    public data class Image(
        /**
         * The File ID of the image in the message content.
         */
        @SerialName("image_file") val imageFile: ImageFile
    ) : MessageContent

}

/**
 * The text content of the message value and annotations.
 */
@BetaOpenAI
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
@BetaOpenAI
@Serializable
public sealed interface TextAnnotation {

    /**
     * The text in the message content that needs to be replaced.
     */
    public val text: String

    /**
     * Start index.
     */
    public val startIndex: Int

    /**
     * End index.
     */
    public val endIndex: Int
}

/**
 * A citation within the message that points to a specific quote from a specific File associated with the assistant or
 * the message. Generated when the assistant uses the "retrieval" tool to search files.
 */
@BetaOpenAI
@Serializable
@SerialName("file_citation")
public data class FileCitationAnnotation(
    @SerialName("text") override val text: String,
    @SerialName("start_index") override val startIndex: Int,
    @SerialName("end_index") override val endIndex: Int,
    /**
     * The specific quote in the file.
     */
    @SerialName("file_citation") val fileCitation: FileCitation,
) : TextAnnotation

@BetaOpenAI
@Serializable
public data class FileCitation(
    /**
     * The ID of the specific File the citation is from.
     */
    @SerialName("file_id") val fileId: FileId,

    /**
     * The specific quote in the file
     */
    @SerialName("quote") val quote: String,
)

/**
 * A URL for the file that's generated when the assistant used the code interpreter tool to generate a file.
 */
@BetaOpenAI
@Serializable
@SerialName("file_path")
public data class FilePathAnnotation(
    @SerialName("text") override val text: String,
    @SerialName("start_index") override val startIndex: Int,
    @SerialName("end_index") override val endIndex: Int,
    /**
     * The file path.
     */
    @SerialName("file_path") val filePath: FilePath,
) : TextAnnotation

@BetaOpenAI
@Serializable
public data class FilePath(
    /**
     * The ID of the file that was generated.
     */
    @SerialName("file_id") val path: FileId
)
