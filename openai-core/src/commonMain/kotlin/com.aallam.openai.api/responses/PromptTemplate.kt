package com.aallam.openai.api.responses

import kotlinx.serialization.Serializable

/**
 * Reference to a prompt template and its variables.
 */
@Serializable
public data class PromptTemplate(

    /** The unique identifier of the prompt template to use. */
    val id: String,

    /**
     * Optional map of values to substitute in for variables in your prompt.
     * The substitution values can either be strings, or other Response input types like images or files.
     */
    val variables: Map<String, ResponseInput>? = null,

    /** Optional version of the prompt tem */
    val version: String? = null
)
