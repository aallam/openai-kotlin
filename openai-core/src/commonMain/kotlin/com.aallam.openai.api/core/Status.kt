package com.aallam.openai.api.core

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * Operation Status.
 */
@Serializable
@JvmInline
public value class Status(public val raw: String)
