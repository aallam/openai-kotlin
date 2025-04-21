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
         * Include the search results of the file search tool call
         */
        public val FileSearchCallResults: ResponseIncludable = ResponseIncludable("file_search_call.results")
        
        /**
         * Include image urls from the input message
         */
        public val MessageInputImageUrl: ResponseIncludable = ResponseIncludable("message.input_image.image_url")
        
        /**
         * Include image urls from the computer call output
         */
        public val ComputerCallOutputImageUrl: ResponseIncludable = ResponseIncludable("computer_call_output.output.image_url")
    }
} 