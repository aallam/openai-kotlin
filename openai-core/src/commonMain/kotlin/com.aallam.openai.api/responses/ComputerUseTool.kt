package com.aallam.openai.api.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Computer action for the computer use tool
 */
@Serializable
public sealed interface ComputerAction {

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

}

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
    val status: ResponseStatus,

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
