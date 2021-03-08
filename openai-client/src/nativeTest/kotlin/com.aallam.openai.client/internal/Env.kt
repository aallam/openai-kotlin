package com.aallam.openai.client.internal

import kotlinx.cinterop.toKString
import platform.posix.getenv

internal actual fun env(name: String): String? {
    return getenv(name)?.toKString()
}
