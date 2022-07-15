package com.aallam.openai.api.finetune

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@Serializable
@JvmInline
public value class FineTuneId(public val id: String)
