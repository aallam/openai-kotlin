package com.aallam.openai.client

import com.aallam.openai.api.core.PaginatedList
import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.finetuning.FineTuningId
import com.aallam.openai.api.finetuning.FineTuningJob
import com.aallam.openai.api.finetuning.FineTuningJobEvent
import com.aallam.openai.api.finetuning.FineTuningRequest
import com.aallam.openai.api.finetuning.FineTuningJobDetails
import com.aallam.openai.api.finetuning.FineTuningCheckpoint
import com.aallam.openai.api.finetuning.FineTuningPage

/**
 * Manage fine-tuning jobs to tailor a model to your specific training data.
 */
public interface FineTuning {

    /**
     * Creates a job that fine-tunes a specified model from a given dataset.
     *
     * The response includes details of the enqueued job including job status and the name of the fine-tuned models once complete.
     *
     * @param request fine-tuning request.
     * @param requestOptions request options.
     */
    public suspend fun fineTuningJob(request: FineTuningRequest, requestOptions: RequestOptions? = null): FineTuningJob

    /**
     * List your organization's fine-tuning jobs.
     *
     * @param after Identifier for the last job from the previous pagination request.
     * @param limit Number of fine-tuning jobs to retrieve.
     * @param requestOptions request options.
     */
    public suspend fun fineTuningJobs(
        after: String? = null,
        limit: Int? = null,
        requestOptions: RequestOptions? = null
    ): List<FineTuningJob>

    /**
     * Get info about a fine-tuning job.
     *
     * @param id The ID of the fine-tuning job.
     * @param requestOptions request options.
     */
    public suspend fun fineTuningJob(id: FineTuningId, requestOptions: RequestOptions? = null): FineTuningJob?

    /**
     * Immediately cancel a fine-tune job.
     *
     * @param id The ID of the fine-tuning job to cancel.
     * @param requestOptions request options.
     */
    public suspend fun cancel(id: FineTuningId, requestOptions: RequestOptions? = null): FineTuningJob?

    /**
     * Get status updates for a fine-tuning job.
     *
     * @param id The ID of the fine-tuning job to get events for.
     * @param after Identifier for the last event from the previous pagination request.
     * @param limit Number of events to retrieve.
     * @param requestOptions request options.
     */
    public suspend fun fineTuningEvents(
        id: FineTuningId,
        after: String? = null,
        limit: Int? = null, requestOptions: RequestOptions? = null
    ): PaginatedList<FineTuningJobEvent>

    /** Retrieves current job details, including nullable hyperparameters and paused states; returns null for HTTP 404. */
    public suspend fun fineTuningJobDetails(id: FineTuningId, requestOptions: RequestOptions? = null): FineTuningJobDetails? =
        throw UnsupportedOperationException("Current fine-tuning operations are not supported by this client.")

    /** Requests a pause and returns current job details. The job can initially be in the pausing state. */
    public suspend fun pauseFineTuningJob(id: FineTuningId, requestOptions: RequestOptions? = null): FineTuningJobDetails =
        throw UnsupportedOperationException("Current fine-tuning operations are not supported by this client.")

    /** Resumes a paused job and returns current job details. */
    public suspend fun resumeFineTuningJob(id: FineTuningId, requestOptions: RequestOptions? = null): FineTuningJobDetails =
        throw UnsupportedOperationException("Current fine-tuning operations are not supported by this client.")

    /** Lists checkpoints, retaining cursor metadata. Use the checkpoint model ID for permission operations. */
    public suspend fun fineTuningJobCheckpoints(
        id: FineTuningId,
        after: String? = null,
        limit: Int? = null,
        requestOptions: RequestOptions? = null,
    ): FineTuningPage<FineTuningCheckpoint> =
        throw UnsupportedOperationException("Current fine-tuning operations are not supported by this client.")
}
