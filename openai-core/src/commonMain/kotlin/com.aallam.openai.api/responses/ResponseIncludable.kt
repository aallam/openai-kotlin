package com.aallam.openai.api.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Additional data to include in the response
 *
 * Specify additional output data to include in the model response.
 */
@Serializable
public enum class ResponseIncludable {
    /**
     * Include the search results of the file search tool call
     */
    @SerialName("file_search_call.results")
    FILE_SEARCH_CALL_RESULTS,
    
    /**
     * Include image urls from the input message
     */
    @SerialName("message.input_image.image_url")
    MESSAGE_INPUT_IMAGE_URL,
    
    /**
     * Include image urls from the computer call output
     */
    @SerialName("computer_call_output.output.image_url")
    COMPUTER_CALL_OUTPUT_IMAGE_URL
} 