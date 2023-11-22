package com.aallam.openai.api.run

import com.aallam.openai.api.BetaOpenAI
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * A run step id.
 */
@BetaOpenAI
@Serializable
@JvmInline
public value class RunStepId(public val id: String)
