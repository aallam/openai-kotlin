package com.aallam.openai.client.internal

import com.aallam.openai.api.core.Status
import com.aallam.openai.api.file.FileId
import com.aallam.openai.client.OpenAI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

internal suspend fun OpenAI.waitFileProcess(fileId: FileId) {
    withContext(Dispatchers.Default) {
        while (true) {
            val file = file(fileId)
            if (file?.status == Status.Processed) break
            delay(1000L)
        }
    }
}
