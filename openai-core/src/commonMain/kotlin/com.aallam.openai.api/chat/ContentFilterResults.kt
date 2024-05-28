package com.aallam.openai.api.chat

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class ContentFilterResult(
    @SerialName("filtered") val filtered: Boolean,
    @SerialName("severity") val severity: String,
)

@Serializable
public data class ContentFilterResults(
    @SerialName("hate") val hate: ContentFilterResult? = null,
    @SerialName("self_harm") val selfHarm: ContentFilterResult? = null,
    @SerialName("sexual") val sexual: ContentFilterResult? = null,
    @SerialName("violence") val violence: ContentFilterResult? = null,
    @SerialName("jailbreak") val jailbreak: ContentFilterResult? = null,
    @SerialName("profanity") val profanity: ContentFilterResult? = null,
)
