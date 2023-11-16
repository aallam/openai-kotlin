package com.aallam.openai.api.message

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * A message id.
 */
@Serializable
@JvmInline
public value class MessageId(public val id: String)
