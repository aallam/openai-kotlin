package com.aallam.openai.client.internal

import io.ktor.client.request.forms.*
import io.ktor.http.*
import okio.ExperimentalFileSystem
import okio.FileSystem
import okio.Path.Companion.toPath

@OptIn(ExperimentalFileSystem::class)
internal fun FormBuilder.appendFile(key: String, filePath: String) {
    val path = filePath.toPath()
    append(key, path.name, ContentType.Application.OctetStream) {
        FileSystem.SYSTEM.read(path) {
            while (true) {
                val line = readUtf8Line() ?: break
                appendLine(line)
            }
        }
    }
}
