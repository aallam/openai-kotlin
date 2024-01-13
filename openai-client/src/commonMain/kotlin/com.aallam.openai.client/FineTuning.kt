package com.aallam.openai.client

import com.aallam.openai.api.core.PaginatedList
import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.finetuning.FineTuningId
import com.aallam.openai.api.finetuning.FineTuningJob
import com.aallam.openai.api.finetuning.FineTuningJobEvent
import com.aallam.openai.api.finetuning.FineTuningRequest

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
}
