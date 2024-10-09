package com.aallam.openai.client

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.core.SortOrder
import com.aallam.openai.api.message.Message
import com.aallam.openai.api.message.MessageId
import com.aallam.openai.api.message.MessageRequest
import com.aallam.openai.api.thread.ThreadId
import io.ktor.http.*

/**
 * Create messages within threads
 */
@BetaOpenAI
public interface Messages {

    /**
     * Create a message.
     *
     * @param threadId the identifier of the thread
     * @param request message creation request
     * @param requestOptions request options.
     */
    @BetaOpenAI
    public suspend fun message(
        threadId: ThreadId,
        request: MessageRequest,
        requestOptions: RequestOptions? = null
    ): Message

    /**
     * Retrieve a message.
     *
     * @param threadId the identifier of the thread
     * @param messageId the identifier of the message
     * @param requestOptions request options.
     */
    @BetaOpenAI
    public suspend fun message(
        threadId: ThreadId,
        messageId: MessageId,
        requestOptions: RequestOptions? = null
    ): Message

    /**
     * Modify a message.
     *
     * @param threadId the identifier of the thread
     * @param messageId the identifier of the message
     * @param metadata set of 16 key-value pairs that can be attached to an object.
     * This can be useful for storing additional information about the object in a structured format.
     * Keys can be a maximum of 64 characters long, and values can be a maximum of 512 characters long.
     * @param requestOptions request options.
     */
    @BetaOpenAI
    public suspend fun message(
        threadId: ThreadId,
        messageId: MessageId,
        metadata: Map<String, String>? = null,
        requestOptions: RequestOptions? = null
    ): Message

    /**
     * Returns a list of messages for a given thread.
     *
     * @param threadId the identifier of the thread
     * @param limit a limit on the number of objects to be returned.
     * The Limit can range between 1 and 100, and the default is 20.
     * @param order sort order by the `created_at` timestamp of the objects.
     * @param after a cursor for use in pagination. [after] is an object ID that defines your place in the list.
     * For instance, if you make a list request and receive 100 objects, ending with `obj_foo`, your subsequent call
     * can include `after = MessageId("obj_foo")` in order to fetch the next page of the list.
     * @param before a cursor for use in pagination. [before] is an object ID that defines your place in the list.
     * For instance, if you make a list request and receive 100 objects, ending with `obj_foo`, your subsequent call
     * can include `before = MessageId("obj_foo")` in order to fetch the previous page of the list.
     * @param requestOptions request options.
     */
    @BetaOpenAI
    public suspend fun messages(
        threadId: ThreadId,
        limit: Int? = null,
        order: SortOrder? = null,
        after: MessageId? = null,
        before: MessageId? = null,
        requestOptions: RequestOptions? = null
    ): List<Message>

    /**
     * Returns a list of messages for a given thread with response headers.
     *
     * @param threadId the identifier of the thread
     * @param limit a limit on the number of objects to be returned.
     * The Limit can range between 1 and 100, and the default is 20.
     * @param order sort order by the `created_at` timestamp of the objects.
     * @param after a cursor for use in pagination. [after] is an object ID that defines your place in the list.
     * For instance, if you make a list request and receive 100 objects, ending with `obj_foo`, your subsequent call
     * can include `after = MessageId("obj_foo")` in order to fetch the next page of the list.
     * @param before a cursor for use in pagination. [before] is an object ID that defines your place in the list.
     * For instance, if you make a list request and receive 100 objects, ending with `obj_foo`, your subsequent call
     * can include `before = MessageId("obj_foo")` in order to fetch the previous page of the list.
     * @param requestOptions request options.
     */
    @BetaOpenAI
    public suspend fun messagesWithHeaders(
        threadId: ThreadId,
        limit: Int? = null,
        order: SortOrder? = null,
        after: MessageId? = null,
        before: MessageId? = null,
        requestOptions: RequestOptions? = null
    ): Pair<Headers, List<Message>>
}
