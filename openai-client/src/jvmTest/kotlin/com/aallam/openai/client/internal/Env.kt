package com.aallam.openai.client.internal

internal actual fun env(name: String): String? {
    return System.getenv(name)
}
