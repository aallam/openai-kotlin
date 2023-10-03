package com.aallam.openai.api.finetuning

import kotlinx.serialization.Serializable

/**
 * For fine-tuning jobs that have `failed`, this will contain more information on the cause of the failure.
 */
@Serializable
public data class ErrorInfo(

    /**
     * A human-readable error message.
     */
    val message: String,

    /**
     * A machine-readable error code.
     */
    val code: String,

    /**
     * The parameter that was invalid (e.g., `training_file`, `validation_file`), or null if not parameter-specific.
     */
    val param: String? = null,
)
