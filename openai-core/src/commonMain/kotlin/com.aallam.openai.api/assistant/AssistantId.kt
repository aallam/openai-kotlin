package com.aallam.openai.api.assistant

import com.aallam.openai.api.BetaOpenAI
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * ID of an assistant.
 */
@BetaOpenAI
@Serializable
@JvmInline
public value class AssistantId(public val id: String)
