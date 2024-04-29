package com.aallam.openai.api.batch

import com.aallam.openai.api.BetaOpenAI
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * The batch identifier.
 */
@BetaOpenAI
@JvmInline
@Serializable
public value class BatchId(public val id: String)
