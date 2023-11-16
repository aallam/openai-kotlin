package com.aallam.openai.api.run

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.file.FileId
import com.aallam.openai.api.run.internal.CodeInterpreterToolCallOutputSerializer
import com.aallam.openai.api.run.internal.RunStepDetailsSerializer
import com.aallam.openai.api.run.internal.ToolCallStepSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@BetaOpenAI
@Serializable(with = RunStepDetailsSerializer::class)
public sealed interface RunStepDetails

/**
 * Details of the message creation by the run step.
 */
@BetaOpenAI
@Serializable
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
public data class MessageCreation(
    /**
     * The ID of the message that was created by this run step.
     */
    @SerialName("message") public val message: String,
)

/**
 * Details of the tool call.
 */
@BetaOpenAI
@Serializable
public data class ToolCallStepDetails(
    /**
     * An array of tool calls the run step was involved in.
     * These can be associated with one of three types of tools:
     * [ToolCallStep.CodeInterpreter], [ToolCallStep.RetrievalTool], or [ToolCallStep.FunctionTool].
     */
    @SerialName("tool_calls") public val toolCalls: List<ToolCallStep>? = null,
) : RunStepDetails

@Serializable(with = ToolCallStepSerializer::class)
public sealed interface ToolCallStep {

    @Serializable
    public data class CodeInterpreter(
        /**
         * The ID of the tool call.
         */
        @SerialName("id") public val id: String,
        /**
         * The Code Interpreter tool call definition.
         */
        @SerialName("code_interpreter") public val codeInterpreter: CodeInterpreterToolCall,
    ) : ToolCallStep

    @Serializable
    public data class RetrievalTool(
        /**
         * The ID of the tool call object.
         */
        @SerialName("id") public val id: String,
        /**
         * For now, this is always going to be an empty object.
         */
        @SerialName("retrieval") public val retrieval: Map<String, String>,
    ) : ToolCallStep

    @Serializable
    public data class FunctionTool(
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
    ) : ToolCallStep
}

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

@Serializable(with = CodeInterpreterToolCallOutputSerializer::class)
public sealed interface CodeInterpreterToolCallOutput

/**
 * Code interpreter log output.
 *
 * Text output from the Code Interpreter tool call as part of a run step.
 */
@Serializable
public data class CodeInterpreterToolCallLogOutput(
    /**
     * The text output from the Code Interpreter tool call.
     */
    @SerialName("text") public val text: String,
) : CodeInterpreterToolCallOutput

/**
 * Code interpreter image output
 */
@Serializable
public data class CodeInterpreterToolCallImageOutput(
    /**
     * The image output from the Code Interpreter tool call.
     */
    @SerialName("image") public val image: CodeInterpreterImage,
) : CodeInterpreterToolCallOutput

/**
 * Code interpreter image
 */
@Serializable
public data class CodeInterpreterImage(
    /**
     * The file ID of the image.
     */
    @SerialName("file_id") public val fileId: FileId,
)
