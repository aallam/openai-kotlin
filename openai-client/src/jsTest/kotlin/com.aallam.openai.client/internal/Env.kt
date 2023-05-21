package com.aallam.openai.client.internal

internal actual fun env(name: String): String? {
    //return js("process.env[name]").unsafeCast<String?>()
    return js("globalThis.process.env[name]") as String?
}
