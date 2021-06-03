package com.aallam.openai.api.file

import com.aallam.openai.api.file.internal.FileStatusSerializer
import kotlinx.serialization.Serializable

/**
 * File status.
 */
@Serializable(FileStatusSerializer::class)
public sealed class FileStatus(public val raw: String) {

    /**
     * File status: uploaded.
     */
    public object Uploaded : FileStatus("uploaded")

    /**
     * File status: processed
     */
    public object Processed : FileStatus("processed")

    /**
     * Other file status.
     */
    public class Custom(status: String) : FileStatus(status)
}
