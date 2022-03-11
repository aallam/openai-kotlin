package com.aallam.openai.api.file

import com.aallam.openai.api.file.internal.FileStatusSerializer
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline
import kotlin.native.concurrent.SharedImmutable

/**
 * File status.
 */
@Serializable(FileStatusSerializer::class)
@JvmInline
public value class FileStatus(public val raw: String)

/**
 * File status: uploaded.
 */
@SharedImmutable
public val Uploaded: FileStatus = FileStatus("uploaded")

/**
 * File status: processed
 */
@SharedImmutable
public val Processed: FileStatus = FileStatus("processed")
