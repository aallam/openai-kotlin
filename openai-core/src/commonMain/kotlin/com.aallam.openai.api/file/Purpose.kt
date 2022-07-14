package com.aallam.openai.api.file

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@Serializable
@JvmInline
public value class Purpose(public val raw: String) {

    public companion object {

        /**
         * File for Searches.
         */
        public val FineTune: Purpose = Purpose("fine-tune")
    }
}
