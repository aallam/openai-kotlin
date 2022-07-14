package com.aallam.openai.api.file

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * File purpose.
 */
@Serializable
@JvmInline
public value class Purpose(public val raw: String)
