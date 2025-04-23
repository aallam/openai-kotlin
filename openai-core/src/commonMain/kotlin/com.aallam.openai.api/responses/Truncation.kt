package com.aallam.openai.api.responses

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * Controls truncation behavior for the model
 *
 * - `auto`: If the context of this response and previous ones exceeds
 *   the model's context window size, the model will truncate the
 *   response to fit the context window by dropping input items in the
 *   middle of the conversation.
 * - `disabled` (default): If a model response will exceed the context window
 *   size for a model, the request will fail with a 400 error.
 */
@JvmInline
@Serializable
public value class Truncation(public val value: String) {
    public companion object {
        /**
         * If the context of this response and previous ones exceeds
         * the model's context window size, the model will truncate the
         * response to fit the context window by dropping input items in the
         * middle of the conversation.
         */
        public val Auto: Truncation = Truncation("auto")

        /**
         * If a model response will exceed the context window
         * size for a model, the request will fail with a 400 error.
         * This is the default.
         */
        public val Disabled: Truncation = Truncation("disabled")
    }
}
