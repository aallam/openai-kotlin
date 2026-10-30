package com.aallam.openai.client.internal.http

import kotlinx.coroutines.flow.Flow

internal interface RealtimeSocket {
    val incoming: Flow<String>
    suspend fun send(text: String)
    suspend fun close()
}
