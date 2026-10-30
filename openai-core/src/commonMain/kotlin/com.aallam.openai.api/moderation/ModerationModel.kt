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

        /** Current omni-moderation model for text and image inputs. */
        public val OmniLatest: ModerationModel = ModerationModel("omni-moderation-latest")

        /** Pinned omni-moderation snapshot from September 26, 2024. */
        public val Omni20240926: ModerationModel = ModerationModel("omni-moderation-2024-09-26")

        /**
         * Retired text-moderation alias, retained for compatibility.
         */
        @Deprecated("The text-moderation models are retired. Use OmniLatest.", ReplaceWith("OmniLatest"))
        public val Stable: ModerationModel = ModerationModel("text-moderation-stable")

        /**
         * Retired text-moderation alias, retained for compatibility.
         */
        @Deprecated("The text-moderation models are retired. Use OmniLatest.", ReplaceWith("OmniLatest"))
        public val Latest: ModerationModel = ModerationModel("text-moderation-latest")
    }
}
