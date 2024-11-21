package com.aallam.openai.client.internal.api

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.core.PaginatedList
import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.core.SortOrder
import com.aallam.openai.api.run.*
import com.aallam.openai.api.thread.ThreadId
import com.aallam.openai.client.Runs
import com.aallam.openai.client.internal.JsonLenient
import com.aallam.openai.client.internal.extension.beta
import com.aallam.openai.client.internal.extension.requestOptions
import com.aallam.openai.client.internal.http.HttpRequester
import com.aallam.openai.client.internal.http.perform
import io.ktor.client.call.*
import io.ktor.client.plugins.sse.ClientSSESession
import io.ktor.client.plugins.sse.sse
import io.ktor.client.plugins.sse.sseSession
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.sse.ServerSentEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.serialization.encodeToString

internal class RunsApi(val requester: HttpRequester) : Runs {
    override suspend fun createRun(threadId: ThreadId, request: RunRequest, requestOptions: RequestOptions?): Run {
        return requester.perform {
            it.post {
                url(path = "${ApiPath.Threads}/${threadId.id}/runs")
                setBody(request.copy(stream = false))
                contentType(ContentType.Application.Json)
                beta("assistants", 2)
                requestOptions(requestOptions)
            }.body()
        }
    }

    @BetaOpenAI
    override suspend fun createStreamingRun(threadId: ThreadId, request: RunRequest, requestOptions: RequestOptions?, block: suspend (AssistantStreamEvent) -> Unit) {
        requester
            .performSse {
                url(path = "${ApiPath.Threads}/${threadId.id}/runs")
                setBody(JsonLenient.encodeToString(RunRequest.serializer(), request.copy(stream = true)))
                contentType(ContentType.Application.Json)
                accept(ContentType.Text.EventStream)
                beta("assistants", 2)
                requestOptions(requestOptions)
                method = HttpMethod.Post
            }
            .mapNotNull { "{ \"event\": \"${it.event}\", \"data\": \"${it.data!!.replace("\"", "\\\"")}\" }" }
            .map { JsonLenient.decodeFromString<AssistantStreamEvent>(it) }
            .onEach(block)
            .collect()
    }

    override suspend fun getRun(threadId: ThreadId, runId: RunId, requestOptions: RequestOptions?): Run {
        return requester.perform {
            it.get {
                url(path = "${ApiPath.Threads}/${threadId.id}/runs/${runId.id}")
                beta("assistants", 2)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun updateRun(
        threadId: ThreadId,
        runId: RunId,
        metadata: Map<String, String>?,
        requestOptions: RequestOptions?
    ): Run {
        return requester.perform {
            it.post {
                url(path = "${ApiPath.Threads}/${threadId.id}/runs/${runId.id}")
                metadata?.let { meta ->
                    setBody(mapOf("metadata" to meta))
                    contentType(ContentType.Application.Json)
                }
                beta("assistants", 2)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun runs(
        threadId: ThreadId,
        limit: Int?,
        order: SortOrder?,
        after: RunId?,
        before: RunId?,
        requestOptions: RequestOptions?
    ): PaginatedList<Run> {
        return requester.perform {
            it.get {
                url(path = "${ApiPath.Threads}/${threadId.id}/runs") {
                    limit?.let { parameter("limit", it) }
                    order?.let { parameter("order", it.order) }
                    after?.let { parameter("after", it.id) }
                    before?.let { parameter("before", it.id) }
                }
                beta("assistants", 2)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun submitToolOutput(
        threadId: ThreadId,
        runId: RunId,
        output: List<ToolOutput>,
        requestOptions: RequestOptions?
    ): Run {
        return requester.perform {
            it.post {
                url(path = "${ApiPath.Threads}/${threadId.id}/runs/${runId.id}/submit_tool_outputs")
                setBody(mapOf("tool_outputs" to output))
                contentType(ContentType.Application.Json)
                beta("assistants", 2)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun cancel(threadId: ThreadId, runId: RunId, requestOptions: RequestOptions?): Run {
        return requester.perform {
            it.post {
                url(path = "${ApiPath.Threads}/${threadId.id}/runs/${runId.id}/cancel")
                beta("assistants", 2)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun createThreadRun(request: ThreadRunRequest, requestOptions: RequestOptions?): Run {
        return requester.perform {
            it.post {
                url(path = "${ApiPath.Threads}/runs")
                setBody(request)
                contentType(ContentType.Application.Json)
                beta("assistants", 2)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun runStep(
        threadId: ThreadId,
        runId: RunId,
        stepId: RunStepId,
        requestOptions: RequestOptions?
    ): RunStep {
        return requester.perform {
            it.get {
                url(path = "${ApiPath.Threads}/${threadId.id}/runs/${runId.id}/steps/${stepId.id}")
                beta("assistants", 2)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun runSteps(
        threadId: ThreadId,
        runId: RunId,
        limit: Int?,
        order: SortOrder?,
        after: RunStepId?,
        before: RunStepId?,
        requestOptions: RequestOptions?
    ): PaginatedList<RunStep> {
        return requester.perform {
            it.get {
                url(path = "${ApiPath.Threads}/${threadId.id}/runs/${runId.id}/steps") {
                    limit?.let { parameter("limit", it) }
                    order?.let { parameter("order", it.order) }
                    after?.let { parameter("after", it.id) }
                    before?.let { parameter("before", it.id) }
                }
                beta("assistants", 2)
                requestOptions(requestOptions)
            }.body()
        }
    }
}
