package com.aallam.openai.api.common

import com.aallam.openai.api.ExperimentalOpenAI
import kotlin.jvm.JvmInline

/**
 * Represents a **local** path to file.
 */
@JvmInline
@ExperimentalOpenAI
public value class FilePath(public val path: String)
