package com.aallam.openai.api.model

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * Model identifier.
 */
@Serializable
@JvmInline
public value class ModelId(public val id: String)
