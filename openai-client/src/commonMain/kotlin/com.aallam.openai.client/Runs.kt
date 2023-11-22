package com.aallam.openai.client

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.core.SortOrder
import com.aallam.openai.api.core.Status
import com.aallam.openai.api.run.*
import com.aallam.openai.api.thread.ThreadId

/**
 * Represents an execution run on a thread.
 */
@BetaOpenAI
public interface Runs {

    /**
     * Create a run.
     *
     * @param threadId The ID of the thread to run
     * @param request request for a run
     *
     */
    @BetaOpenAI
    public suspend fun createRun(threadId: ThreadId, request: RunRequest): Run

    /**
     * Retrieves a run.
     *
     * @param threadId The ID of the thread to run
     * @param runId The ID of the run to retrieve
     */
    @BetaOpenAI
    public suspend fun retrieveRun(threadId: ThreadId, runId: RunId): Run

    /**
     * Retrieves a run.
     *
     * @param threadId the ID of the thread to run
     * @param runId the ID of the run to retrieve
     * @param metadata set of 16 key-value pairs that can be attached to an object.
     * This can be useful for storing additional information about the object in a structured format.
     * Keys can be a maximum of 64 characters long, and values can be a maximum of 512 characters long.
     */
    @BetaOpenAI
    public suspend fun modifyRun(threadId: ThreadId, runId: RunId, metadata: Map<String, String>? = null): Run

    /**
     * Returns a list of runs belonging to a thread.
     *
     * @param threadId the ID of the thread the run belongs to
     * @param order sort order by the `created_at` timestamp of the objects.
     * @param after a cursor for use in pagination. [after] is an object ID that defines your place in the list.
     * For instance, if you make a list request and receive 100 objects, ending with `obj_foo`, your subsequent call
     * can include `after = RunId("obj_foo")` in order to fetch the next page of the list.
     * @param before a cursor for use in pagination. [before] is an object ID that defines your place in the list.
     * For instance, if you make a list request and receive 100 objects, ending with `obj_foo`, your subsequent call
     * can include `before = RunId("obj_foo")` in order to fetch the previous page of the list.
     */
    @BetaOpenAI
    public suspend fun listRuns(
        threadId: ThreadId,
        limit: Int? = null,
        order: SortOrder? = null,
        after: RunId? = null,
        before: RunId? = null,
    ): List<Run>

    /**
     * When a run has the status: [Status.RequiresAction] and required action is [RequiredAction.SubmitToolOutputs],
     * this endpoint can be used to submit the outputs from the tool calls once they're all completed.
     * All outputs must be submitted in a single request.
     */
    @BetaOpenAI
    public suspend fun submitToolOutput(threadId: ThreadId, runId: RunId, output: List<ToolOutput>): Run

    /**
     * Cancels a run that is [Status.InProgress].
     *
     * @param threadId the ID of the thread to which this run belongs
     * @param runId the ID of the run to cancel
     */
    @BetaOpenAI
    public suspend fun cancel(threadId: ThreadId, runId: String): Run

    /**
     * Create a thread and run it in one request.
     *
     * @param request request for a thread run
     */
    @BetaOpenAI
    public suspend fun createThreadRun(request: RunRequest): Run

    /**
     * Retrieves a run step.
     *
     * @param threadId the ID of the thread to which the run and run step belongs
     * @param runId the ID of the run to which the run step belongs
     * @param stepId the ID of the run step to retrieve
     */
    @BetaOpenAI
    public suspend fun runStep(threadId: ThreadId, runId: RunId, stepId: RunStepId): RunStep

    /**
     * Retrieves a run step.
     *
     * @param threadId the ID of the thread to which the run and run step belongs
     * @param runId the ID of the run to which the run step belongs
     * @param limit A limit on the number of objects to be returned.
     * The Limit can range between 1 and 100, and the default is 20.
     * @param order sort order by the `created_at` timestamp of the objects.
     * @param after a cursor for use in pagination. [after] is an object ID that defines your place in the list.
     * For instance, if you make a list request and receive 100 objects, ending with `obj_foo`, your subsequent call
     * can include `after = RunId("obj_foo")` in order to fetch the next page of the list.
     * @param before a cursor for use in pagination. [before] is an object ID that defines your place in the list.
     * For instance, if you make a list request and receive 100 objects, ending with `obj_foo`, your subsequent call
     * can include `before = RunId("obj_foo")` in order to fetch the previous page of the list.
     */
    @BetaOpenAI
    public suspend fun runSteps(
        threadId: ThreadId,
        runId: RunId,
        limit: Int? = null,
        order: SortOrder? = null,
        after: RunStepId? = null,
        before: RunStepId? = null,
    ): List<RunStep>
}
