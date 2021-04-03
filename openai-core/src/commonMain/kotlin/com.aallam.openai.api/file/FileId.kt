package com.aallam.openai.api.file

import com.aallam.openai.api.file.internal.FileIdSerializer
import kotlinx.serialization.Serializable

@Serializable(FileIdSerializer::class)
public data class FileId(val raw: String) {
    override fun toString(): String {
        return raw
    }
}
