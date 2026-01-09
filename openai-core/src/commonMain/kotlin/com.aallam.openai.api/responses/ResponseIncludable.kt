package com.aallam.openai.api.responses

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * Additional data to include in the response
 *
 * Specify additional output data to include in the model response.
 */
@JvmInline
@Serializable
public value class ResponseIncludable(public val value: String) {
    public companion object {

        /**
         *  Includes the outputs of python code execution in code interpreter tool call items.
         */
        public val CodeInterpreterCallOutputs: ResponseIncludable = ResponseIncludable("code_interpreter_call.outputs")

        /**
         * Include image urls from the computer call output.
         */
        public val ComputerCallOutputImageUrl: ResponseIncludable = ResponseIncludable("computer_call_output.output.image_url")

        /**
         * Include the search results of the file search tool call
         */
        public val FileSearchCallResults: ResponseIncludable = ResponseIncludable("file_search_call.results")

        /**
         * Include image urls from the input message
         */
        public val MessageInputImageUrl: ResponseIncludable = ResponseIncludable("message.input_image.image_url")

        /**
         * Include logprobs with assistant messages.
         */
        public val MessageOutputTextLogprobs: ResponseIncludable = ResponseIncludable("message.output_text.logprobs")

        /**
         * Includes an encrypted version of reasoning tokens in reasoning item outputs.
         * This enables reasoning items to be used in multi-turn conversations when using the Responses API statelessly
         * (like when the store parameter is set to false, or when an organization is enrolled in the zero data retention program).
         */
        public val ReasoningEncryptedContent: ResponseIncludable = ResponseIncludable("reasoning.encrypted_content")
    }
}
