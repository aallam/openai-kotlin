package com.aallam.openai.api.edits

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public class Usage(
    @SerialName("prompt_tokens") public val promptTokens: Int,
    @SerialName("completion_tokens") public val completionTokens: Int,
    @SerialName("total_tokens") public val totaTokens: Int
)
