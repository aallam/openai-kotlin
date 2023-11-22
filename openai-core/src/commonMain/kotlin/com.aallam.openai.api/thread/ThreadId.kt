package com.aallam.openai.api.thread

import com.aallam.openai.api.BetaOpenAI
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * Thread identifier.
 */
@BetaOpenAI
@JvmInline
@Serializable
public value class ThreadId(public val id: String)
