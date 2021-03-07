package com.aallam.openai.client.internal

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.asPromise
import kotlinx.coroutines.async

actual fun runBlockingTest(block: suspend CoroutineScope.() -> Unit): dynamic {
    return GlobalScope.async(block = block).asPromise()
}
