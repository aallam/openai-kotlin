package com.aallam.openai.api.response

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * The response identifier.
 */
@JvmInline
@Serializable
public value class ResponseId(public val id: String)
