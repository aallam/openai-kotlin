package com.aallam.openai.api.batch

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatCompletionRequest
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * The per-line object of the batch input file.
 */
@BetaOpenAI
@Serializable
public data class RequestInput(
    /**
     * A developer-provided per-request id that will be used to match outputs to inputs.
     * Must be unique for each request in a batch.
     */
    @SerialName("custom_id") public val customId: CustomId,

    /**
     * The HTTP method to be used for the request. Currently only [Method.Post] is supported.
     */
    @SerialName("method") public val method: Method,

    /**
     * The OpenAI API relative URL to be used for the request.
     * Currently only `/v1/chat/completions` is supported.
     */
    @SerialName("url") public val url: String,

    /**
     * The body of the request.
     */
    @SerialName("body") public val body: ChatCompletionRequest? = null,
)
