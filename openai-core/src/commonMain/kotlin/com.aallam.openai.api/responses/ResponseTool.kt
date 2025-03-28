package com.aallam.openai.api.responses

import com.aallam.openai.api.core.Parameters
import com.aallam.openai.api.vectorstore.VectorStoreId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
         * The type of web search tool - must be "web_search_preview"
         */
        @SerialName("type")
        val type: String = "web_search_preview",
        
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
         * The type of web search tool - must be "web_search_preview_2025_03_11"
         */
        @SerialName("type")
        val type: String = "web_search_preview_2025_03_11",
        
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
         * The type of computer use tool - must be "computer_use_preview"
         */
        @SerialName("type")
        val type: String = "computer_use_preview",
        
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
    public data class Function(
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
public sealed interface ComputerAction {
    /**
     * The type of the computer action
     */
    public val type: String
}

/**
 * A click action
 */
@Serializable
@SerialName("click")
public data class Click(
    /**
     * The type of the action. Always "click".
     */
    @SerialName("type")
    override val type: String = "click",
    
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
     * The type of the action. Always "double_click".
     */
    @SerialName("type")
    override val type: String = "double_click",
    
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
     * The type of the action. Always "drag".
     */
    @SerialName("type")
    override val type: String = "drag",
    
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
     * The type of the action. Always "keypress".
     */
    @SerialName("type")
    override val type: String = "keypress",
    
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
     * The type of the action. Always "move".
     */
    @SerialName("type")
    override val type: String = "move",
    
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
public data class Screenshot(
    /**
     * The type of the action. Always "screenshot".
     */
    @SerialName("type")
    override val type: String = "screenshot"
) : ComputerAction

/**
 * A scroll action
 */
@Serializable
@SerialName("scroll")
public data class Scroll(
    /**
     * The type of the action. Always "scroll".
     */
    @SerialName("type")
    override val type: String = "scroll",
    
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
     * The type of the action. Always "type".
     */
    @SerialName("type")
    override val type: String = "type",
    
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
public data class Wait(
    /**
     * The type of the action. Always "wait".
     */
    @SerialName("type")
    override val type: String = "wait"
) : ComputerAction

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