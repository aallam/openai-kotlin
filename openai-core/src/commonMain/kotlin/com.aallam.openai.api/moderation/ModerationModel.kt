package com.aallam.openai.api.moderation

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * Moderation model.
 */
@JvmInline
@Serializable
public value class ModerationModel(public val model: String) {

    public companion object {

        /**
         * Ensures you are always using the most accurate model.
         */
        public val Stable: ModerationModel = ModerationModel("text-moderation-stable")

        /**
         * Advanced notice is provided before updating this model.
         */
        public val Latest: ModerationModel = ModerationModel("text-moderation-latest")
    }
}
