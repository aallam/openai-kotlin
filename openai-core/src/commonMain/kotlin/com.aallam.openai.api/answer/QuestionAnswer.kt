@file:Suppress("DEPRECATION")

package com.aallam.openai.api.answer

import com.aallam.openai.api.ExperimentalOpenAI
import com.aallam.openai.api.answer.internal.QuestionAnswerSerializer
import kotlinx.serialization.Serializable

@Deprecated("Answers APIs are deprecated")
@ExperimentalOpenAI
@Serializable(QuestionAnswerSerializer::class)
public data class QuestionAnswer(
    val question: String,
    val answer: String
)
