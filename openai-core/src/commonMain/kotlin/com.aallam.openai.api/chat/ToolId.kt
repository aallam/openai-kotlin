package com.aallam.openai.api.chat

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * The ID of the tool call.
 */
@JvmInline
@Serializable
public value class ToolId(public val id: String)
