package com.aallam.openai.api.file

import com.aallam.openai.api.file.internal.FileStatusSerializer
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * File status.
 */
@Serializable(FileStatusSerializer::class)
@JvmInline
public value class FileStatus(public val raw: String)

/**
 * File status: uploaded.
 */
public val Uploaded: FileStatus = FileStatus("uploaded")

/**
 * File status: processed
 */
public val Processed: FileStatus = FileStatus("processed")
