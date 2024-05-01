package com.aallam.openai.api.batch

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * Custom ID for the request.
 */
@Serializable
@JvmInline
public value class CustomId(public val value: String)
