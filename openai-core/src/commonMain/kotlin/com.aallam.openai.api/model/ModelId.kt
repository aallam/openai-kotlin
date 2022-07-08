package com.aallam.openai.api.model

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * OpenAI’s Model ID.
 */
@Serializable
@JvmInline
public value class ModelId(public val id: String) {
    override fun toString(): String = id
}
