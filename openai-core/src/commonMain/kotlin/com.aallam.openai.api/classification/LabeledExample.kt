package com.aallam.openai.api.classification

import com.aallam.openai.api.ExperimentalOpenAI
import com.aallam.openai.api.classification.internal.LabeledExampleSerializer
import kotlinx.serialization.Serializable

@Deprecated("Classification APIs are deprecated")
@ExperimentalOpenAI
@Serializable(LabeledExampleSerializer::class)
public data class LabeledExample(
    public val example: String,
    public val label: String
)
