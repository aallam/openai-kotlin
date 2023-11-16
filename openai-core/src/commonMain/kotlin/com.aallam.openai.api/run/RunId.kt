package com.aallam.openai.api.run

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * A run id.
 */
@Serializable
@JvmInline
public value class RunId(public val id: String)
