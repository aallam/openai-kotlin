package com.aallam.openai.api.file

import com.aallam.openai.api.file.internal.PurposeSerializer
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@Serializable(PurposeSerializer::class)
@JvmInline
public value class Purpose(public val raw: String)

/**
 * File for Searches.
 */
public val Search: Purpose = Purpose("search")

/**
 * File for Answers.
 */
public val Answers: Purpose = Purpose("answers")

/**
 * File for classifications.
 */
public val Classifications: Purpose = Purpose("classifications")
