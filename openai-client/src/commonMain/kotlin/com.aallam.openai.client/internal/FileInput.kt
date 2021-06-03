package com.aallam.openai.client.internal

import io.ktor.client.request.forms.*
import io.ktor.http.*
import okio.FileSystem
import okio.Path.Companion.toPath

internal fun FormBuilder.appendFile(fileSystem: FileSystem, key: String, filePath: String) {
    val path = filePath.toPath()
    append(key, path.name, ContentType.Application.OctetStream) {
        fileSystem.read(path) {
            while (true) {
                val line = readUtf8Line() ?: break
                appendLine(line)
            }
        }
    }
}

internal expect val ClientFileSystem: FileSystem
