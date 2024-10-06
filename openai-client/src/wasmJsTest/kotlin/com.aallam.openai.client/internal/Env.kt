package com.aallam.openai.client.internal

internal actual fun env(name: String): String? {
    return getEnv(name)
}

fun getEnv(value: String): String? = js("""globalThis.process.env[value]""")