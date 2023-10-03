package com.aallam.openai.client.internal

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.toKString
import platform.posix.getenv

@OptIn(ExperimentalForeignApi::class)
internal actual fun env(name: String): String? {
    return getenv(name)?.toKString()
}
