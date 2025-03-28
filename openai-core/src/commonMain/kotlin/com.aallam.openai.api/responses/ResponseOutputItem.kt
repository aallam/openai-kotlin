package com.aallam.openai.api.responses

import com.aallam.openai.api.core.Role
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

/**
 * A single output item in the response
 */
@Serializable
public sealed interface ResponseOutputItem {
    /**
     * The type of output item
     */
    public val type: String

    /**
     * The ID of the output item
     */
    public val id: String

    /**
     * The status of the item, one of "in_progress", "completed", or "incomplete".
     */
    public val status: ResponseStatus
}

/**
 * An output message from the model.
 */
@Serializable
@SerialName("message")
public data class OutputMessage(
    /**
     * The unique ID of the output message.
     */
    @SerialName("id")
    override val id: String,

    /**
     * The type of the output message. Always "message".
     */
    @SerialName("type")
    override val type: String = "message",

    /**
     * The role of the output message. Always "assistant".
     */
    @SerialName("role")
    val role: Role = Role.Assistant,

    /**
     * The content of the output message.
     */
    @SerialName("content")
    val content: List<OutputContent>,

    /**
     * The status of the message. One of "in_progress", "completed", or "incomplete".
     */
    @SerialName("status")
    override val status: ResponseStatus

) : ResponseOutputItem

/**
 * Content part in an output message
 */
@Serializable
public sealed interface OutputContent {
    /**
     * The type of content
     */
    public val type: String
}

/**
 * Text output from the model
 */
@Serializable
@SerialName("output_text")
public data class OutputText(
    /**
     * The type of the output text. Always "output_text".
     */
    @SerialName("type")
    override val type: String = "output_text",

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
) : OutputContent

/**
 * Refusal message from the model
 */
@Serializable
@SerialName("refusal")
public data class Refusal(
    /**
     * The type of the refusal. Always "refusal".
     */
    @SerialName("type")
    override val type: String = "refusal",

    /**
     * The refusal explanation from the model.
     */
    @SerialName("refusal")
    val refusal: String
) : OutputContent

/**
 * An annotation in text output
 */
@Serializable
public sealed interface Annotation {
    /**
     * The type of annotation
     */
    public val type: String
}

/**
 * A citation to a file.
 */
@Serializable
@SerialName("file_citation")
public data class FileCitation(
    /**
     * The type of annotation. Always "file_citation".
     */
    @SerialName("type")
    override val type: String = "file_citation",

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
     * The type of annotation. Always "url_citation".
     */
    @SerialName("type")
    override val type: String = "url_citation",

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
     * The type of annotation. Always "file_path".
     */
    @SerialName("type")
    override val type: String = "file_path",

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
 * File search tool call in a response
 */
@Serializable
@SerialName("file_search_call")
public data class FileSearchToolCall(
    /**
     * The unique ID of the file search tool call.
     */
    @SerialName("id")
    override val id: String,

    /**
     * The type of the file search tool call. Always "file_search_call".
     */
    @SerialName("type")
    override val type: String = "file_search_call",

    /**
     * The status of the file search tool call.
     */
    @SerialName("status")
    override val status: ResponseStatus,

    /**
     * The queries used to search for files.
     */
    @SerialName("queries")
    val queries: List<String>,

    /**
     * The results of the file search tool call.
     */
    @SerialName("results")
    val results: List<FileSearchResult>? = null
) : ResponseOutputItem

/**
 * Function tool call in a response
 */
@Serializable
@SerialName("function_call")
public data class FunctionToolCall(
    /**
     * The unique ID of the function tool call.
     */
    @SerialName("id")
    override val id: String,

    /**
     * The type of the function tool call. Always "function_call".
     */
    @SerialName("type")
    override val type: String = "function_call",

    /**
     * The status of the function tool call.
     */
    @SerialName("status")
    override val status: ResponseStatus,

    /**
     * The unique ID of the function tool call generated by the model.
     */
    @SerialName("call_id")
    val callId: String,

    /**
     * The name of the function to run.
     */
    @SerialName("name")
    val name: String,

    /**
     * A JSON string of the arguments to pass to the function.
     */
    @SerialName("arguments")
    val arguments: String,
) : ResponseOutputItem {

    /**
     * Decodes the [arguments] JSON string into a JsonObject.
     * If [arguments] is null, the function will return null.
     *
     * @param json The Json object to be used for decoding, defaults to a default Json instance
     */
    public fun argumentsAsJson(json: Json = Json): JsonObject = json.decodeFromString(arguments)

}

/**
 * Web search tool call in a response
 */
@Serializable
@SerialName("web_search_call")
public data class WebSearchToolCall(
    /**
     * The unique ID of the web search tool call.
     */
    @SerialName("id")
    override val id: String,

    /**
     * The type of the web search tool call. Always "web_search_call".
     */
    @SerialName("type")
    override val type: String = "web_search_call",

    /**
     * The status of the web search tool call.
     */
    @SerialName("status")
    override val status: ResponseStatus
) : ResponseOutputItem

/**
 * Computer tool call in a response
 */
@Serializable
@SerialName("computer_call")
public data class ComputerToolCall(
    /**
     * The unique ID of the computer tool call.
     */
    @SerialName("id")
    override val id: String,

    /**
     * The type of the computer call. Always "computer_call".
     */
    @SerialName("type")
    override val type: String = "computer_call",

    /**
     * The status of the computer tool call.
     */
    @SerialName("status")
    override val status: ResponseStatus,

    /**
     * An identifier used when responding to the tool call with output.
     */
    @SerialName("call_id")
    val callId: String,

    /**
     * The action to be performed
     */
    @SerialName("action")
    val action: ComputerAction,

    /**
     * The pending safety checks for the computer call.
     */
    @SerialName("pending_safety_checks")
    val pendingSafetyChecks: List<SafetyCheck> = emptyList()
) : ResponseOutputItem

/**
 * A safety check for a computer call
 */
@Serializable
public data class SafetyCheck(
    /**
     * The ID of the safety check
     */
    @SerialName("id")
    val id: String,

    /**
     * The type code of the safety check
     */
    @SerialName("code")
    val code: String,

    /**
     * The message about the safety check
     */
    @SerialName("message")
    val message: String
)

/**
 * Reasoning item for model reasoning
 */
@Serializable
@SerialName("reasoning")
public data class ReasoningItem(
    /**
     * The unique ID of the reasoning item.
     */
    @SerialName("id")
    override val id: String,

    /**
     * The type of the object. Always "reasoning".
     */
    @SerialName("type")
    override val type: String = "reasoning",

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
) : ResponseOutputItem

/**
 * A summary text item in the reasoning output
 */
@Serializable
public data class SummaryText(
    /**
     * The type of the summary text. Always "summary_text".
     */
    @SerialName("type")
    val type: String = "summary_text",

    /**
     * A short summary of the reasoning used by the model.
     */
    @SerialName("text")
    val text: String
)

/**
 * Status of an output item
 */
@Serializable
public enum class ResponseStatus {
    @SerialName("in_progress")
    IN_PROGRESS,

    @SerialName("completed")
    COMPLETED,

    @SerialName("incomplete")
    INCOMPLETE
}