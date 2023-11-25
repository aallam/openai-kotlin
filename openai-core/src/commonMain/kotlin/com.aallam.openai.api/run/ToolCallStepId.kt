package com.aallam.openai.api.run

import com.aallam.openai.api.BetaOpenAI
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * Tool call step identifier.
 */
@BetaOpenAI
@JvmInline
@Serializable
public value class ToolCallStepId(public val id: String)
