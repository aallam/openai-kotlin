package com.aallam.openai.api.core

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * An unique identifier for the OpenAI API request.
 */
@Serializable
@JvmInline
public value class RequestId(public val id: String)
