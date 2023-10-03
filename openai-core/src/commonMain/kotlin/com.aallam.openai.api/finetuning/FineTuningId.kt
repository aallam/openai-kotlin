package com.aallam.openai.api.finetuning

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * Fine-tuning identifier.
 */
@Serializable
@JvmInline
public value class FineTuningId(public val id: String)
