package com.aallam.openai.client

import com.aallam.openai.api.core.PaginatedList
import com.aallam.openai.api.finetuning.*

/**
 * Manage fine-tuning jobs to tailor a model to your specific training data.
 */
public interface FineTuning {

    /**
     * Creates a job that fine-tunes a specified model from a given dataset.
     *
     * Response includes details of the enqueued job including job status and the name of the fine-tuned models once complete.
     */
    public suspend fun fineTuningJob(request: FineTuningRequest): FineTuningJob

    /**
     * List your organization's fine-tuning jobs.
     *
     * @param after Identifier for the last job from the previous pagination request.
     * @param limit Number of fine-tuning jobs to retrieve.
     */
    public suspend fun fineTuningJobs(after: String? = null, limit: Int? = null): List<FineTuningJob>

    /**
     * Get info about a fine-tuning job.
     *
     * @param id The ID of the fine-tuning job.
     */
    public suspend fun fineTuningJob(id: FineTuningId): FineTuningJob?

    /**
     * Immediately cancel a fine-tune job.
     *
     * @param id The ID of the fine-tuning job to cancel.
     */
    public suspend fun cancel(id: FineTuningId): FineTuningJob?

    /**
     * Get status updates for a fine-tuning job.
     *
     * @param id The ID of the fine-tuning job to get events for.
     * @param after Identifier for the last event from the previous pagination request.
     * @param limit Number of events to retrieve.
     */
    public suspend fun fineTuningEvents(
        id: FineTuningId,
        after: String? = null,
        limit: Int? = null
    ): PaginatedList<FineTuningJobEvent>
}
