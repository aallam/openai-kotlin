package com.aallam.openai.api.chat

import com.aallam.openai.api.core.Usage
import com.aallam.openai.api.model.ModelId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * An object containing a response from the chat stream completion api.
 *
 * [documentation](https://platform.openai.com/docs/api-reference/chat/create)
 */
@Serializable
public data class ChatCompletionChunk(
    /**
     * A unique id assigned to this completion
     */
    @SerialName("id")
    public val id: String? = null,

    /**
     * The creation time in epoch milliseconds.
     */
    @SerialName("created")
    public val created: Long,

    /**
     * The model used.
     */
    @SerialName("model")
    public val model: ModelId,

    /**
     * A list of generated completions
     */
    @SerialName("choices")
    public val choices: List<ChatChunk>,

    /**
     * Text completion usage data.
     */
    @SerialName("usage")
    public val usage: Usage? = null,

    /**
     * This fingerprint represents the backend configuration that the model runs with. Can be used in conjunction with
     * the `seed` request parameter to understand when backend changes have been made that might impact determinism.
     */
    @SerialName("system_fingerprint")
    public val systemFingerprint: String? = null,

    @SerialName("citations")
    public val citations: List<String>? = null,
)
