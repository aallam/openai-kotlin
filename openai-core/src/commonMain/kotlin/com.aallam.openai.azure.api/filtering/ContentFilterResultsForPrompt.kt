package com.aallam.openai.azure.api.filtering

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

/**
 * Content filtering results for a single prompt in the request.
 */
@Serializable
public data class ContentFilterResultsForPrompt(

    /**
     * The index of this prompt in the set of prompt results.
     */
    @SerialName("prompt_index")
    val promptIndex: Int,

    /**
     * Content filtering results for this prompt.
     */
    @SerialName("content_filter_results")
    val contentFilterResults: ContentFilterResultDetailsForPrompt
)
