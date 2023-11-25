package com.aallam.openai.api.message

import com.aallam.openai.api.BetaOpenAI
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * A message id.
 */
@BetaOpenAI
@Serializable
@JvmInline
public value class MessageId(public val id: String)
