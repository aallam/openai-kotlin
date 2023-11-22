package com.aallam.openai.client.internal.api

import com.aallam.openai.api.core.PaginatedList
import com.aallam.openai.api.core.SortOrder
import com.aallam.openai.api.run.*
import com.aallam.openai.api.thread.ThreadId
import com.aallam.openai.client.Runs
import com.aallam.openai.client.internal.extension.beta
import com.aallam.openai.client.internal.http.HttpRequester
import com.aallam.openai.client.internal.http.perform
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

internal class RunsApi(val requester: HttpRequester) : Runs {
    override suspend fun createRun(threadId: ThreadId, request: RunRequest): Run {
        return requester.perform {
            it.post {
                url(path = "${ApiPath.Threads}/${threadId.id}/runs")
                setBody(request)
                contentType(ContentType.Application.Json)
                beta("assistants", 1)
            }.body()
        }
    }

    override suspend fun retrieveRun(threadId: ThreadId, runId: RunId): Run {
        return requester.perform {
            it.get {
                url(path = "${ApiPath.Threads}/${threadId.id}/runs/${runId.id}")
                beta("assistants", 1)
            }.body()
        }
    }

    override suspend fun modifyRun(threadId: ThreadId, runId: RunId, metadata: Map<String, String>?): Run {
        return requester.perform {
            it.post {
                url(path = "${ApiPath.Threads}/${threadId.id}/runs/${runId.id}")
                metadata?.let { meta ->
                    setBody(mapOf("metadata" to meta))
                    contentType(ContentType.Application.Json)
                }
                beta("assistants", 1)
            }.body()
        }
    }

    override suspend fun runs(
        threadId: ThreadId,
        limit: Int?,
        order: SortOrder?,
        after: RunId?,
        before: RunId?
    ): PaginatedList<Run> {
        return requester.perform {
            it.get {
                url(path = "${ApiPath.Threads}/${threadId.id}/runs") {
                    limit?.let { parameter("limit", it) }
                    order?.let { parameter("order", it) }
                    after?.let { parameter("after", it.id) }
                    before?.let { parameter("before", it.id) }
                }
                beta("assistants", 1)
            }.body()
        }
    }

    override suspend fun submitToolOutput(threadId: ThreadId, runId: RunId, output: List<ToolOutput>): Run {
        return requester.perform {
            it.post {
                url(path = "${ApiPath.Threads}/${threadId.id}/runs/${runId.id}/submit_tool_outputs")
                setBody(mapOf("tool_outputs" to output))
                contentType(ContentType.Application.Json)
                beta("assistants", 1)
            }.body()
        }
    }

    override suspend fun cancel(threadId: ThreadId, runId: RunId): Run {
        return requester.perform {
            it.post {
                url(path = "${ApiPath.Threads}/${threadId.id}/runs/${runId.id}/cancel")
                beta("assistants", 1)
            }.body()
        }
    }

    override suspend fun createThreadRun(request: RunRequest): Run {
        return requester.perform {
            it.post {
                url(path = "${ApiPath.Threads}/runs")
                setBody(request)
                contentType(ContentType.Application.Json)
                beta("assistants", 1)
            }.body()
        }
    }

    override suspend fun runStep(threadId: ThreadId, runId: RunId, stepId: RunStepId): RunStep {
        return requester.perform {
            it.get {
                url(path = "${ApiPath.Threads}/${threadId.id}/runs/${runId.id}/steps/${stepId.id}")
                beta("assistants", 1)
            }.body()
        }
    }

    override suspend fun runSteps(
        threadId: ThreadId,
        runId: RunId,
        limit: Int?,
        order: SortOrder?,
        after: RunStepId?,
        before: RunStepId?
    ): PaginatedList<RunStep> {
        return requester.perform {
            it.get {
                url(path = "${ApiPath.Threads}/${threadId.id}/runs/${runId.id}/steps") {
                    limit?.let { parameter("limit", it) }
                    order?.let { parameter("order", it.order) }
                    after?.let { parameter("after", it.id) }
                    before?.let { parameter("before", it.id) }
                }
                beta("assistants", 1)
            }.body()
        }
    }
}