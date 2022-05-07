package com.aallam.openai.api.finetunes

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@Serializable
@JvmInline
public value class FineTuneModel(public val raw: String)
