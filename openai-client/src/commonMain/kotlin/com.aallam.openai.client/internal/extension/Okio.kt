package com.aallam.openai.client.internal.extension

import io.ktor.utils.io.core.BytePacketBuilder
import io.ktor.utils.io.core.writeFully
import okio.BufferedSource

// https://github.com/square/okio/blob/a94c678de4e8a21e53126d42a1a3d897daa56a4a/recipes/index.html#L1322
private const val OKIO_BUFFER_SIZE = 8192
internal fun BytePacketBuilder.writeAll(source: BufferedSource) {
    val buffer = ByteArray(OKIO_BUFFER_SIZE)
    var bytesRead: Int
    while (source.read(buffer).also { bytesRead = it } != -1) {
        writeFully(src = buffer, offset = 0, length = bytesRead)
    }
}
