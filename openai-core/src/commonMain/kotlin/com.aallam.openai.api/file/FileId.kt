package com.aallam.openai.api.file

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@Serializable
@JvmInline
public value class FileId(public val raw: String)
