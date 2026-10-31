package com.aallam.openai.client

import com.aallam.openai.client.internal.withTestCleanup
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

class TestOfflineCleanup {
    @Test
    fun cancelsJobsBeforeDeletingTheirInputFiles() = runTest {
        val actions = mutableListOf<String>()
        withTestCleanup {
            afterTest { actions.add("delete file") }
            afterTest { actions.add("cancel job") }
            actions.add("assertions")
        }
        assertEquals(listOf("assertions", "cancel job", "delete file"), actions)
    }

    @Test
    fun assertionFailureStillCleansEveryResourceAndRemainsThePrimaryFailure() = runTest {
        val actions = mutableListOf<String>()
        val assertionFailure = AssertionError("assertion failed")
        val cancelFailure = IllegalStateException("cancel failed")
        val failure = assertFailsWith<AssertionError> {
            withTestCleanup {
                afterTest { actions.add("delete file") }
                afterTest {
                    actions.add("cancel job")
                    throw cancelFailure
                }
                throw assertionFailure
            }
        }
        assertSame(assertionFailure, failure)
        assertEquals(listOf(cancelFailure), failure.suppressedExceptions)
        assertEquals(listOf("cancel job", "delete file"), actions)
    }

    @Test
    fun cleanupFailureFailsAnOtherwiseSuccessfulTestAfterAllResourcesAreAttempted() = runTest {
        val actions = mutableListOf<String>()
        val cancelFailure = IllegalStateException("cancel failed")
        val deleteFailure = IllegalArgumentException("delete failed")
        val failure = assertFailsWith<IllegalStateException> {
            withTestCleanup {
                afterTest {
                    actions.add("delete file")
                    throw deleteFailure
                }
                afterTest {
                    actions.add("cancel job")
                    throw cancelFailure
                }
            }
        }
        assertSame(cancelFailure, failure)
        assertEquals(listOf(deleteFailure), failure.suppressedExceptions)
        assertEquals(listOf("cancel job", "delete file"), actions)
    }

    @Test
    fun cancellationAllowsSuspendingCleanupToFinish() = runTest {
        val actions = mutableListOf<String>()
        val test = launch {
            withTestCleanup {
                afterTest {
                    delay(1)
                    actions.add("delete file")
                }
                afterTest {
                    delay(1)
                    actions.add("cancel job")
                }
                awaitCancellation()
            }
        }
        runCurrent()
        test.cancelAndJoin()
        assertEquals(listOf("cancel job", "delete file"), actions)
    }
}
