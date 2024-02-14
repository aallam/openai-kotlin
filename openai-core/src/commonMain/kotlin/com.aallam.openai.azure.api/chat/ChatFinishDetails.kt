package com.aallam.openai.azure.api.chat

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * An abstract representation of structured information about why a chat completions response terminated.
 */
@Serializable
public sealed class ChatFinishDetails {

    /**
     * Represents the "stop" type of ChatFinishDetails.
     */
    @Serializable
    @SerialName("stop")
    public data class StopFinishDetails(

        /**
         * The token sequence that the model terminated with.
         */
        @SerialName("stop")
        val stop: String
    ) : ChatFinishDetails()

    /**
     * A structured representation of a stop reason that signifies a token limit was reached before the model could
     * naturally complete.
     */
    @Serializable
    @SerialName("max_tokens")
    public class MaxTokensFinishDetails : ChatFinishDetails()
}
