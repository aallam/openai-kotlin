package com.aallam.openai.client.internal

import okio.Buffer
import okio.Source

internal fun String.asSource(): Source {
    val buffer = Buffer()
    buffer.writeUtf8(this)
    return buffer
}
