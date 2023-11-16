package com.aallam.openai.api.thread

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * Thread identifier.
 */
@JvmInline
@Serializable
public value class ThreadId(public val id: String)
