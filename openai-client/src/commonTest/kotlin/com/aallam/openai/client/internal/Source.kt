package com.aallam.openai.client.internal

import kotlinx.io.Buffer
import kotlinx.io.Source
import kotlinx.io.writeString

internal fun String.asSource(): Source {
    val buffer = Buffer()
    buffer.writeString(this)
    return buffer
}
