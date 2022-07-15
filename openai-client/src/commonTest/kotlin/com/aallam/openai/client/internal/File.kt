package com.aallam.openai.client.internal

import com.aallam.openai.api.core.Status
import com.aallam.openai.api.file.FileId
import com.aallam.openai.client.OpenAI
import kotlinx.coroutines.delay

internal suspend fun OpenAI.waitFileProcess(fileId: FileId) {
    while (true) {
        val file = file(fileId)
        if (file?.status == Status.Processed) break
        delay(1000L)
    }
}
