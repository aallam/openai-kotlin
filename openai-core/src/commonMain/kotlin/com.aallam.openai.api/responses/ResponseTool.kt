package com.aallam.openai.api.responses

import com.aallam.openai.api.core.Parameters
import com.aallam.openai.api.responses.ResponseTool.*
import com.aallam.openai.api.vectorstore.VectorStoreId
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Required
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*
import kotlin.jvm.JvmInline

/**
 * An array of tools the model may call while generating a response.
 */
@Serializable
public sealed interface ResponseTool {

    /**
     * A tool that searches for relevant content from uploaded files.
     */
    @Serializable
    @SerialName("file_search")
    public data class FileSearch(
        /**
         * The IDs of the vector stores to search.
         */
        @SerialName("vector_store_ids")
        val vectorStoreIds: List<VectorStoreId> = emptyList(),
        /**
         * A filter to apply based on file attributes.
         */
        @SerialName("filters")
        val filters: FileSearchFilter? = null,

        /**
         * Ranking options for search.
         */
        @SerialName("ranking_options")
        val rankingOptions: FileSearchRankingOptions? = null,

        /**
         * The maximum number of results to return. This number should be between 1 and 50 inclusive.
         */
        @SerialName("max_num_results")
        val maxNumResults: Int? = null,
    ) : ResponseTool


    @Serializable(
        with = FileSearchFilterSerializer::class
    )
    public sealed interface FileSearchFilter

    /**
     * A filter used to compare a specified attribute key to a given value using a defined comparison operation.
     */
    @Serializable
    public data class ComparisonFilter(

        /**
         * Specifies the comparison operator: eq, ne, gt, gte, lt, lte.
         */
        @SerialName("type")
        public val type: String,

        /**
         * The key to compare against the value.
         */
        @SerialName("key")
        public val key: String,

        /**
         * The value to compare the attribute key to.
         */
        @SerialName("value")
        public val value: String,

        ) : FileSearchFilter

    /**
     * Combine multiple filters using 'and' or 'or'.
     */
    @Serializable
    public data class CompoundFilter(
        /**
         * The logical operator to use: 'and' or 'or'.
         */
        @SerialName("type")
        public val type: String,

        /**
         * Array of filters to combine. Items can be ComparisonFilter or CompoundFilter.
         */
        @SerialName("filters")
        public val filters: List<FileSearchFilter>,

        ) : FileSearchFilter

    /**
     * Ranking options for search.
     */
    @Serializable
    public data class FileSearchRankingOptions(
        /**
         *The ranker to use for the file search.
         * Defaults to "auto"
         */
        @SerialName("ranker")
        val ranker: String? = null,

        /**
         * The score threshold for the file search, a number between 0 and 1.
         * Numbers closer to 1 will attempt to return only the most relevant results, but may return fewer results.
         * Defaults to 0
         */
        @SerialName("score_threshold")
        val scoreThreshold: Int? = null,
    )


    /**
     * Web search tool (preview)
     */
    @Serializable
    @SerialName("web_search_preview")
    public data class WebSearchPreview(
        /**
         * Approximate location parameters for the search.
         */
        @SerialName("user_location")
        val userLocation: WebSearchLocation? = null,

        /**
         * High level guidance for the amount of context window space to use for the search.
         * One of 'low', 'medium', or 'high'.
         * 'medium' is the default.
         */
        @SerialName("search_context_size")
        val searchContextSize: WebSearchContextSize? = null,
    ) : ResponseTool

    /**
     * Web search tool (preview 2025-03-11)
     */
    @Serializable
    @SerialName("web_search_preview_2025_03_11")
    public data class WebSearchPreview2025(

        /**
         * Approximate location parameters for the search.
         */
        @SerialName("user_location")
        val userLocation: WebSearchLocation? = null,

        /**
         * High level guidance for the amount of context window space to use for the search.
         * One of 'low', 'medium', or 'high'.
         * 'medium' is the default.
         */
        @SerialName("search_context_size")
        val searchContextSize: WebSearchContextSize? = null,
    ) : ResponseTool


    /**
     * Web search context size
     */
    @JvmInline
    @Serializable
    public value class WebSearchContextSize(public val value: String) {
        public companion object {
            /**
             * Low context size
             */
            public val Low: WebSearchContextSize = WebSearchContextSize("low")

            /**
             * Medium context size
             */
            public val Medium: WebSearchContextSize = WebSearchContextSize("medium")

            /**
             * High context size
             */
            public val High: WebSearchContextSize = WebSearchContextSize("high")
        }
    }

    /**
     * Web search location
     */
    @Serializable
    public data class WebSearchLocation(
        /**
         * Free text input for the city of the user, e.g., San Francisco.
         */
        @SerialName("city")
        val city: String? = null,

        /**
         * The two-letter ISO-country code of the user, e.g., US.
         */
        @SerialName("country")
        val country: String? = null,

        /**
         * Free text input for the region of the user, e.g., California.
         */
        @SerialName("region")
        val region: String? = null,

        /**
         * The IANA time zone of the user, e.g., America/Los_Angeles.
         */
        @SerialName("timezone")
        val timezone: String? = null,

        ) {
        /**
         * The type of location approximation. Always approximate.
         */
        @SerialName("type")
        @Required
        val type: String = "approximate"
    }

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
        @SerialName("description") val description: String? = null,

        /**
         * Whether to enforce strict parameter validation. Default true.
         */
        @SerialName("strict") val strict: Boolean? = null,
    ) : ResponseTool
}

internal class FileSearchFilterSerializer : KSerializer<FileSearchFilter> {

    override val descriptor = buildClassSerialDescriptor("FileSearchFilter")

    override fun serialize(encoder: Encoder, value: FileSearchFilter) {
        val jsonEncoder = encoder as? JsonEncoder
            ?: throw IllegalArgumentException("This serializer can only be used with JSON")

        when (value) {
            is ComparisonFilter -> ComparisonFilter.serializer().serialize(jsonEncoder, value)
            is CompoundFilter -> CompoundFilter.serializer().serialize(jsonEncoder, value)
        }
    }

    override fun deserialize(decoder: Decoder): FileSearchFilter {
        val jsonDecoder = decoder as? JsonDecoder
            ?: throw IllegalArgumentException("This serializer can only be used with JSON")

        return when (val type = jsonDecoder.decodeJsonElement().jsonObject["type"]?.jsonPrimitive?.content) {
            "and" -> ComparisonFilter.serializer().deserialize(jsonDecoder)
            "or" -> CompoundFilter.serializer().deserialize(jsonDecoder)
            "eq" -> ComparisonFilter.serializer().deserialize(jsonDecoder)
            "ne" -> ComparisonFilter.serializer().deserialize(jsonDecoder)
            "gt" -> ComparisonFilter.serializer().deserialize(jsonDecoder)
            "gte" -> ComparisonFilter.serializer().deserialize(jsonDecoder)
            "lt" -> ComparisonFilter.serializer().deserialize(jsonDecoder)
            "lte" -> ComparisonFilter.serializer().deserialize(jsonDecoder)
            else -> throw IllegalArgumentException("Unknown filter type: $type")
        }
    }

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
