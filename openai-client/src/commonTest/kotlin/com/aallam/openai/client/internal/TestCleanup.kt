package com.aallam.openai.client.internal

import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext

internal suspend fun withTestCleanup(block: suspend TestCleanup.() -> Unit) {
    val cleanup = TestCleanup()
    var failure: Throwable? = null
    try {
        cleanup.block()
    } catch (cause: Throwable) {
        failure = cause
        throw cause
    } finally {
        cleanup.run(failure)
    }
}

internal class TestCleanup {
    private val actions = mutableListOf<suspend () -> Unit>()

    fun afterTest(action: suspend () -> Unit) {
        actions.add(action)
    }

    suspend fun run(originalFailure: Throwable?) {
        var failure = originalFailure
        withContext(NonCancellable) {
            for (action in actions.asReversed()) {
                try {
                    action()
                } catch (cause: Throwable) {
                    val previous = failure
                    if (previous == null) failure = cause else if (previous !== cause) previous.addSuppressed(cause)
                }
            }
        }
        if (originalFailure == null) failure?.let { throw it }
    }
}
