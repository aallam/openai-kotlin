package com.aallam.openai.client.internal.api

import com.aallam.openai.api.core.SortOrder
import com.aallam.openai.api.file.FileId
import com.aallam.openai.api.message.Message
import com.aallam.openai.api.message.MessageFile
import com.aallam.openai.api.message.MessageId
import com.aallam.openai.api.message.MessageRequest
import com.aallam.openai.api.thread.ThreadId
import com.aallam.openai.client.Messages
import com.aallam.openai.client.internal.http.HttpRequester

internal class MessagesApi(requester: HttpRequester) : Messages {
    override suspend fun message(threadId: ThreadId, message: MessageRequest): Message {
        TODO("Not yet implemented")
    }

    override suspend fun message(threadId: ThreadId, messageId: MessageId): Message {
        TODO("Not yet implemented")
    }

    override suspend fun message(threadId: ThreadId, messageId: MessageId, metadata: Map<String, String>?): Message {
        TODO("Not yet implemented")
    }

    override suspend fun messages(
        threadId: ThreadId,
        limit: Int?,
        order: SortOrder?,
        after: MessageId?,
        before: MessageId?
    ): List<Message> {
        TODO("Not yet implemented")
    }

    override suspend fun messageFile(threadId: ThreadId, messageId: MessageId, fileId: FileId): MessageFile {
        TODO("Not yet implemented")
    }

    override suspend fun messageFiles(
        threadId: ThreadId,
        messageId: MessageId,
        limit: Int?,
        order: SortOrder?,
        after: FileId?,
        before: FileId?
    ) {
        TODO("Not yet implemented")
    }
}