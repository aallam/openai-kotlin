package com.aallam.openai.api.responses

import com.aallam.openai.api.core.Parameters
import com.aallam.openai.api.vectorstore.VectorStoreId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

/**
 * An array of tools the model may call while generating a response.
 */
@Serializable
public sealed interface ResponseTool {

    /**
     * File search tool for searching through files
     */
    @Serializable
    @SerialName("file_search")
    public data class FileSearch(
        /**
         * The vector store IDs to search in
         */
        @SerialName("vector_store_ids")
        val vectorStoreIds: List<VectorStoreId> = emptyList(),

        /**
         * Maximum number of results to return
         */
        @SerialName("max_num_results")
        val maxNumResults: Int? = null,
    ) : ResponseTool

    /**
     * Web search tool (preview)
     */
    @Serializable
    @SerialName("web_search_preview")
    public data class WebSearchPreview(
        /**
         * User location information (optional)
         */
        @SerialName("user_location")
        val userLocation: Map<String, String>? = null,

        /**
         * Search context size
         */
        @SerialName("search_context_size")
        val searchContextSize: String? = null
    ) : ResponseTool

    /**
     * Web search tool (preview 2025-03-11)
     */
    @Serializable
    @SerialName("web_search_preview_2025_03_11")
    public data class WebSearchPreview2025(

        /**
         * User location information (optional)
         */
        @SerialName("user_location")
        val userLocation: Map<String, String>? = null,

        /**
         * Search context size
         */
        @SerialName("search_context_size")
        val searchContextSize: String? = null
    ) : ResponseTool

    /**
     * Computer tool for computational tasks (preview)
     */
    @Serializable
    @SerialName("computer_use_preview")
    public data class ComputerUsePreview(

        /**
         * The width of the computer display
         */
        @SerialName("display_width")
        val displayWidth: Int,

        /**
         * The height of the computer display
         */
        @SerialName("display_height")
        val displayHeight: Int,

        /**
         * The type of computer environment to control
         */
        @SerialName("environment")
        val environment: String
    ) : ResponseTool

    /**
     * Function tool for function calling
     */
    @Serializable
    @SerialName("function")
    public data class ResponseFunctionTool(
        /**
         * The name of the function to be called. Must be a-z, A-Z, 0-9, or contain underscores and dashes, with a maximum
         * length of 64.
         */
        @SerialName("name") val name: String,

        /**
         * The parameters the functions accept, described as a JSON Schema object.
         * See the [guide](https://platform.openai.com/docs/guides/text-generation/function-calling) for examples,
         * and the [JSON Schema reference](https://json-schema.org/understanding-json-schema) for documentation about
         * the format.
         *
         * Omitting `parameters` defines a function with an empty parameter list.
         */
        @SerialName("parameters") val parameters: Parameters? = null,

        /**
         * A description of what the function does, used by the model to choose when and how to call the function.
         */
        @SerialName("description") val description: String? = null
    ) : ResponseTool
}

/**
 * Computer action for the computer use tool
 */
@Serializable
public sealed interface ComputerAction
/**
 * A click action
 */
@Serializable
@SerialName("click")
public data class Click(

    /**
     * The mouse button used for the click.
     * One of "left", "right", "wheel", "back", or "forward"
     */
    @SerialName("button")
    val button: String,

    /**
     * The x-coordinate where the click occurred
     */
    @SerialName("x")
    val x: Int,

    /**
     * The y-coordinate where the click occurred
     */
    @SerialName("y")
    val y: Int
) : ComputerAction

/**
 * A double click action
 */
@Serializable
@SerialName("double_click")
public data class DoubleClick(

    /**
     * The x-coordinate where the double click occurred
     */
    @SerialName("x")
    val x: Int,

    /**
     * The y-coordinate where the double click occurred
     */
    @SerialName("y")
    val y: Int
) : ComputerAction

/**
 * A drag action
 */
@Serializable
@SerialName("drag")
public data class Drag(

    /**
     * An array of coordinates representing the path of the drag action
     */
    @SerialName("path")
    val path: List<Coordinate>
) : ComputerAction

/**
 * A keypress action
 */
@Serializable
@SerialName("keypress")
public data class KeyPress(

    /**
     * The combination of keys to press
     */
    @SerialName("keys")
    val keys: List<String>
) : ComputerAction

/**
 * A move action
 */
@Serializable
@SerialName("move")
public data class Move(

    /**
     * The x-coordinate to move to
     */
    @SerialName("x")
    val x: Int,

    /**
     * The y-coordinate to move to
     */
    @SerialName("y")
    val y: Int
) : ComputerAction

/**
 * A screenshot action
 */
@Serializable
@SerialName("screenshot")
public data object Screenshot : ComputerAction

/**
 * A scroll action
 */
@Serializable
@SerialName("scroll")
public data class Scroll(

    /**
     * The x-coordinate where the scroll occurred
     */
    @SerialName("x")
    val x: Int,

    /**
     * The y-coordinate where the scroll occurred
     */
    @SerialName("y")
    val y: Int,

    /**
     * The horizontal scroll distance
     */
    @SerialName("scroll_x")
    val scrollX: Int,

    /**
     * The vertical scroll distance
     */
    @SerialName("scroll_y")
    val scrollY: Int
) : ComputerAction

/**
 * A typing action
 */
@Serializable
@SerialName("type")
public data class Type(

    /**
     * The text to type
     */
    @SerialName("text")
    val text: String
) : ComputerAction

/**
 * A wait action
 */
@Serializable
@SerialName("wait")
public data object Wait : ComputerAction

/**
 * A coordinate pair (x, y)
 */
@Serializable
public data class Coordinate(
    /**
     * The x-coordinate
     */
    @SerialName("x")
    val x: Int,

    /**
     * The y-coordinate
     */
    @SerialName("y")
    val y: Int
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
) : ResponseOutput

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
) : ResponseOutput {

    /**
     * Decodes the [arguments] JSON string into a JsonObject.
     * If [arguments] is null, the function will return null.
     *
     * @param json The Json object to be used for decoding, defaults to a default Json instance
     */
    public fun argumentsAsJson(json: Json = Json): JsonObject = json.decodeFromString(arguments)

}

/**
 * The output of a function tool call.
 *
 */
@Serializable
@SerialName("function_call_output")
public data class FunctionToolCallOutput(

    /**
     * The unique ID of the function tool call output. Populated when this item is returned via API.
     */
    @SerialName("id")
    val id: String? = null,

    /**
     * The unique ID of the function tool call generated by the model.
     */
    @SerialName("call_id")
    val callId: String,

    /**
     * A JSON string of the output of the function tool call.
     */
    @SerialName("output")
    val output: String,

    /**
     * The status of the item. One of in_progress, completed, or incomplete. Populated when items are returned via API.
     */
    @SerialName("status")
    val status: ResponseStatus? = null
) : ResponseItem

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
     * The status of the web search tool call.
     */
    @SerialName("status")
    override val status: ResponseStatus
) : ResponseOutput

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
) : ResponseOutput

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
 * The output of a computer tool call.
 */
@Serializable
@SerialName("computer_call_output")
public data class ComputerToolCallOutput(
    /**
     * The unique ID of the computer tool call output.
     */
    @SerialName("id")
    val id: String? = null,

    /**
     * The ID of the computer tool call that produced the output.
     */
    @SerialName("call_id")
    val callId: String,

    /**
     * A computer screenshot image used with the computer use tool.
     */
    @SerialName("output")
    val output: ComputerScreenshot,

    /**
     * The safety checks reported by the API that have been acknowledged by the developer.
     */
    @SerialName("acknowledged_safety_checks")
    val acknowledgedSafetyChecks: List<SafetyCheck> = emptyList(),

    /**
     * The status of the item. One of in_progress, completed, or incomplete. Populated when items are returned via API.
     */
    @SerialName("status")
    val status: ResponseStatus? = null
) : ResponseItem

/**
 * A computer screenshot image used with the computer use tool.
 */
@Serializable
@SerialName("computer_screenshot")
public data class ComputerScreenshot(

    /**
     * The identifier of an uploaded file that contains the screenshot.
     */
    @SerialName("file_id")
    val fileId: String? = null,

    /**
     * The URL of the screenshot image.
     */
    @SerialName("image_url")
    val imageUrl: String? = null
)