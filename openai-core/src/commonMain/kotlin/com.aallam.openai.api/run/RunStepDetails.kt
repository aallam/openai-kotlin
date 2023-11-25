package com.aallam.openai.api.run

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.file.FileId
import com.aallam.openai.api.message.MessageId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A run step object.
 */
@BetaOpenAI
@Serializable
public sealed interface RunStepDetails

/**
 * Details of the message creation by the run step.
 */
@BetaOpenAI
@Serializable
@SerialName("message_creation")
public data class MessageCreationStepDetails(
    /**
     * The message creation details.
     */
    @SerialName("message_creation") public val messageCreation: MessageCreation,
) : RunStepDetails

/**
 * Details of the message that occurred during the run step.
 */
@Serializable
@BetaOpenAI
public data class MessageCreation(
    /**
     * The ID of the message that was created by this run step.
     */
    @SerialName("message_id") public val messageId: MessageId,
)

/**
 * Details of the tool call.
 */
@BetaOpenAI
@Serializable
@SerialName("tool_calls")
public data class ToolCallStepDetails(
    /**
     * An array of tool calls the run step was involved in.
     * These can be associated with one of three types of tools:
     * [ToolCallStep.CodeInterpreter], [ToolCallStep.RetrievalTool], or [ToolCallStep.FunctionTool].
     */
    @SerialName("tool_calls") public val toolCalls: List<ToolCallStep>? = null,
) : RunStepDetails

@BetaOpenAI
@Serializable
public sealed interface ToolCallStep {

    @BetaOpenAI
    @Serializable
    @SerialName("code_interpreter")
    public data class CodeInterpreter(
        /**
         * The ID of the tool call.
         */
        @SerialName("id") public val id: ToolCallStepId,
        /**
         * The Code Interpreter tool call definition.
         */
        @SerialName("code_interpreter") public val codeInterpreter: CodeInterpreterToolCall,
    ) : ToolCallStep

    @BetaOpenAI
    @Serializable
    @SerialName("retrieval")
    public data class RetrievalTool(
        /**
         * The ID of the tool call object.
         */
        @SerialName("id") public val id: ToolCallStepId,
        /**
         * For now, this is always going to be an empty object.
         */
        @SerialName("retrieval") public val retrieval: Map<String, String>,
    ) : ToolCallStep

    @BetaOpenAI
    @Serializable
    @SerialName("function")
    public data class FunctionTool(
        /**
         * The ID of the tool call object.
         */
        @SerialName("id") public val id: ToolCallStepId,
        /**
         * The definition of the function that was called.
         */
        @SerialName("function") public val function: FunctionToolCallStep,
    ) : ToolCallStep
}

@BetaOpenAI
@Serializable
public data class FunctionToolCallStep(
    /**
     * The name of the function.
     */
    @SerialName("name") public val name: String,

    /**
     * The arguments passed to the function.
     */
    @SerialName("arguments") public val arguments: String,

    /**
     * The output of the function. This will be null if the outputs have not been submitted yet.
     */
    @SerialName("output") public val output: String? = null,
)

@BetaOpenAI
@Serializable
public data class CodeInterpreterToolCall(
    /**
     * The input to the Code Interpreter tool call.
     */
    val input: String,

    /**
     * The outputs from the Code Interpreter tool call. Code Interpreter can output one or more items, including a text
     * (logs) or images (image). Each of these is represented by a different object type.
     */
    val outputs: List<CodeInterpreterToolCallOutput>
)

@BetaOpenAI
@Serializable
public sealed interface CodeInterpreterToolCallOutput {
    /**
     * Code interpreter log output.
     *
     * Text output from the Code Interpreter tool call as part of a run step.
     */
    @BetaOpenAI
    @Serializable
    @SerialName("logs")
    public data class Logs(
        /**
         * The text output from the Code Interpreter tool call.
         */
        @SerialName("text") public val text: String? = null,
    ) : CodeInterpreterToolCallOutput

    /**
     * Code interpreter image output
     */
    @BetaOpenAI
    @Serializable
    @SerialName("image")
    public data class Image(
        /**
         * The image output from the Code Interpreter tool call.
         */
        @SerialName("image") public val image: CodeInterpreterImage,
    ) : CodeInterpreterToolCallOutput

}

/**
 * Code interpreter image
 */
@BetaOpenAI
@Serializable
public data class CodeInterpreterImage(
    /**
     * The file ID of the image.
     */
    @SerialName("file_id") public val fileId: FileId,
)
