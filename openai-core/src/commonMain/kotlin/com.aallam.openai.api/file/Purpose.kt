package com.aallam.openai.api.file

import com.aallam.openai.api.file.internal.PurposeSerializer
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline
import kotlin.native.concurrent.SharedImmutable

@Serializable(PurposeSerializer::class)
@JvmInline
public value class Purpose(public val raw: String)

/**
 * File for Searches.
 */
@SharedImmutable
public val Search: Purpose = Purpose("search")

/**
 * File for Answers.
 */
@SharedImmutable
public val Answers: Purpose = Purpose("answers")

/**
 * File for classifications.
 */
@SharedImmutable
public val Classifications: Purpose = Purpose("classifications")
