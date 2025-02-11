package com.aallam.openai.client.internal.api

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.core.PaginatedList
import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.core.SortOrder
import com.aallam.openai.api.run.*
import com.aallam.openai.api.thread.ThreadId
import com.aallam.openai.client.Runs
import com.aallam.openai.client.internal.extension.beta
import com.aallam.openai.client.internal.extension.requestOptions
import com.aallam.openai.client.internal.http.HttpRequester
import com.aallam.openai.client.internal.http.perform
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.*

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
    override suspend fun createStreamingRun(threadId: ThreadId, request: RunRequest, requestOptions: RequestOptions?) : Flow<AssistantStreamEvent> {
        return requester
            .performSse {
                url(path = "${ApiPath.Threads}/${threadId.id}/runs")
                setBody(request.copy(stream = true))
                contentType(ContentType.Application.Json)
                accept(ContentType.Text.EventStream)
                beta("assistants", 2)
                requestOptions(requestOptions)
                method = HttpMethod.Post
            }
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

    @BetaOpenAI
    override suspend fun submitStreamingToolOutput(
        threadId: ThreadId,
        runId: RunId,
        output: List<ToolOutput>,
        requestOptions: RequestOptions?
    ) : Flow<AssistantStreamEvent> {
        return requester
            .performSse {
                url(path = "${ApiPath.Threads}/${threadId.id}/runs/${runId.id}/submit_tool_outputs")
                val bodyObject = buildJsonObject {
                    put("tool_outputs", output.toJsonArray())
                    put("stream", JsonPrimitive(true))
                }
                setBody(bodyObject)
                contentType(ContentType.Application.Json)
                accept(ContentType.Text.EventStream)
                beta("assistants", 2)
                requestOptions(requestOptions)
                method = HttpMethod.Post
            }
    }

    // Need to manual convert to JsonObject as Kotlinx serialization fails when multiple data types are present in the same collection
    // java.lang.IllegalStateException: Serializing collections of different element types is not yet supported. Selected serializers: [kotlin.collections.ArrayList, kotlin.Boolean]
    // https://youtrack.jetbrains.com/issue/KTOR-3063/Support-serializing-collections-of-different-element-types
    private fun List<ToolOutput>.toJsonArray(): JsonArray = buildJsonArray {
        forEach { add(it.toJsonObject()) }
    }

    private fun ToolOutput.toJsonObject(): JsonObject = buildJsonObject {
        toolCallId?.id?.let { put("tool_call_id", JsonPrimitive(it)) }
        output?.let { put("output", JsonPrimitive(it)) }
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
                setBody(request.copy(stream = false))
                contentType(ContentType.Application.Json)
                beta("assistants", 2)
                requestOptions(requestOptions)
            }.body()
        }
    }

    @BetaOpenAI
    override suspend fun createStreamingThreadRun(
        request: ThreadRunRequest,
        requestOptions: RequestOptions?
    ) : Flow<AssistantStreamEvent> {
        return requester
            .performSse {
                url(path = "${ApiPath.Threads}/runs")
                setBody(request.copy(stream = true))
                contentType(ContentType.Application.Json)
                accept(ContentType.Text.EventStream)
                beta("assistants", 2)
                requestOptions(requestOptions)
                method = HttpMethod.Post
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
