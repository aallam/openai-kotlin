package com.aallam.openai.client.internal.api

import com.aallam.openai.api.core.SortOrder
import com.aallam.openai.api.run.*
import com.aallam.openai.api.thread.ThreadId
import com.aallam.openai.client.Runs
import com.aallam.openai.client.internal.http.HttpRequester

internal class RunsApi(requester: HttpRequester) : Runs {
    override suspend fun createRun(threadId: ThreadId, request: RunRequest): Run {
        TODO("Not yet implemented")
    }

    override suspend fun retrieveRun(threadId: ThreadId, runId: String): Run {
        TODO("Not yet implemented")
    }

    override suspend fun modifyRun(threadId: ThreadId, runId: String, metadata: Map<String, String>?): Run {
        TODO("Not yet implemented")
    }

    override suspend fun listRuns(threadId: ThreadId, order: SortOrder?, after: RunId?, before: RunId?): List<Run> {
        TODO("Not yet implemented")
    }

    override suspend fun submitToolOutput(threadId: ThreadId, runId: String, output: List<ToolOutput>): Run {
        TODO("Not yet implemented")
    }

    override suspend fun cancel(threadId: ThreadId, runId: String): Run {
        TODO("Not yet implemented")
    }

    override suspend fun createThreadRun(request: RunRequest): Run {
        TODO("Not yet implemented")
    }

    override suspend fun getRunStep(threadId: ThreadId, runId: RunId, stepId: RunStepId): RunStep {
        TODO("Not yet implemented")
    }

    override suspend fun listRunSteps(
        threadId: ThreadId,
        runId: RunId,
        order: SortOrder?,
        after: RunStepId?,
        before: RunStepId?
    ): List<RunStep> {
        TODO("Not yet implemented")
    }
}