package com.aallam.openai.api.finetunes

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@Serializable
@JvmInline
public value class FineTuneId(public val raw: String)
