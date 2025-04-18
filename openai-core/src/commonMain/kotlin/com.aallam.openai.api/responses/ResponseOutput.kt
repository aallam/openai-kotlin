package com.aallam.openai.api.responses

import com.aallam.openai.api.core.Role
import kotlinx.serialization.Required
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A single output item in the response
 */
@Serializable
public sealed interface ResponseOutput : ResponseItem {

    /**
     * The ID of the output item.
     * Will always be populated when coming from the API. It is optional here, so you can construct your own OutputMessages
     */
    @SerialName("id")
    public val id: String?

    /**
     * The status of the item, one of "in_progress", "completed", or "incomplete".
     * Will always be populated when coming from the AP. It is optional here, so you can construct your own OutputMessages
     */
    @SerialName("status")
    public val status: ResponseStatus?
}

/**
 * An output message from the model.
 */
@Serializable
@SerialName("message")
public data class ResponseOutputMessage(
    /**
     * The unique ID of the output message.
     */
    @SerialName("id")
    override val id: String? = null,

    /**
     * The content of the output message.
     */
    @SerialName("content")
    public val content: List<ResponseOutputContent>,

    /**
     * The status of the message. One of "in_progress", "completed", or "incomplete".
     */
    @SerialName("status")
    override val status: ResponseStatus? = null,

    ) : ResponseOutput {

    /**
     * The role of the output message. Always "assistant".
     */
    @SerialName("role")
    @Required
    public val role: Role = ResponseRole.Assistant
}

/**
 * Content part in an output message
 */
@Serializable
public sealed interface ResponseOutputContent

/**
 * Text output from the model
 */
@Serializable
@SerialName("output_text")
public data class ResponseOutputText(

    /**
     * The text output from the model.
     */
    @SerialName("text")
    val text: String,

    /**
     * The annotations of the text output.
     */
    @SerialName("annotations")
    val annotations: List<Annotation> = emptyList()
) : ResponseOutputContent

/**
 * Refusal message from the model
 */
@Serializable
@SerialName("refusal")
public data class Refusal(

    /**
     * The refusal explanation from the model.
     */
    @SerialName("refusal")
    val refusal: String
) : ResponseOutputContent

/**
 * An annotation in text output
 */
@Serializable
public sealed interface Annotation

/**
 * A citation to a file.
 */
@Serializable
@SerialName("file_citation")
public data class FileCitation(
    /**
     * The ID of the file.
     *
     */
    @SerialName("file_id")
    val fileCitation: String,

    /**
     * The index of the file in the list of files.
     */
    @SerialName("start_index")
    val index: Int

) : Annotation

/**
 * A citation for a web resource used to generate a model response.
 */
@Serializable
@SerialName("url_citation")
public data class UrlCitation(

    /**
     * The title of the web resource.
     */
    @SerialName("title")
    val title: String,

    /**
     * The URL of the web resource.
     */
    @SerialName("url")
    val url: String,

    /**
     * The index of the first character of the URL citation in the message.
     */
    @SerialName("start_index")
    val startIndex: Int,

    /**
     * The index of the last character of the URL citation in the message.
     */
    @SerialName("end_index")
    val endIndex: Int
) : Annotation

/**
 * A path to a file.
 */
@Serializable
@SerialName("file_path")
public data class FilePath(

    /**
     * File path details
     */
    @SerialName("file_path")
    val filePath: Map<String, String>,

    /**
     * The start index of the file path in the text
     */
    @SerialName("start_index")
    val startIndex: Int,

    /**
     * The end index of the file path in the text
     */
    @SerialName("end_index")
    val endIndex: Int
) : Annotation

/**
 * Result of a file search
 */
@Serializable
public data class FileSearchResult(
    /**
     * The ID of the file
     */
    @SerialName("file_id")
    val fileId: String,

    /**
     * The text content from the file
     */
    @SerialName("text")
    val text: String,

    /**
     * The filename
     */
    @SerialName("filename")
    val filename: String,

    /**
     * The score or relevance rating
     */
    @SerialName("score")
    val score: Double
)


/**
 * Reasoning item for model reasoning
 */
@Serializable
@SerialName("reasoning")
public data class Reasoning(
    /**
     * The unique ID of the reasoning item.
     */
    @SerialName("id")
    override val id: String,

    /**
     * The status of the reasoning item.
     */
    @SerialName("status")
    override val status: ResponseStatus,

    /**
     * Reasoning text contents.
     */
    @SerialName("summary")
    val summary: List<SummaryText>
) : ResponseOutput

/**
 * A summary text item in the reasoning output
 */
@Serializable
@SerialName("summary_text")
public data class SummaryText(

    /**
     * A short summary of the reasoning used by the model.
     */
    @SerialName("text")
    val text: String
)

