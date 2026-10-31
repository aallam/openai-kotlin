package com.aallam.openai.client

import com.aallam.openai.api.live.*
import com.aallam.openai.api.model.ModelId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlin.test.*

class TestLive : TestOpenAI() {
    @Test
    fun primarySessionStartsAndClosesWithoutAudioOrDelegatedWork() = test {
        withContext(Dispatchers.Default) {
            withTimeout(10_000) {
                var started = false
                var closed = false
                var usage: LiveUsage? = null
                openAI.liveSession(LiveSessionConfig(ModelId("gpt-live-1"), delegation = LiveDelegation.Client())) {
                    events.takeWhile { event ->
                        when (event.type) {
                            "session.started" -> {
                                assertNotNull(event.sessionId)
                                started = true
                                send(LiveEvent.closeSession())
                            }
                            "session.closed" -> { closed = true; usage = event.usage }
                            "error" -> error(event.error?.message ?: "Live protocol error")
                        }
                        event.type != "session.closed"
                    }.collect()
                }
                assertTrue(started)
                assertTrue(closed)
                val seconds = assertNotNull(usage).seconds
                assertTrue(seconds >= 0)
                println("OPENAI_LIVE_USAGE {\"operation\":\"live.sessions\",\"model\":\"gpt-live-1\",\"requests\":1,\"audio_seconds\":$seconds}")
            }
        }
    }
}
