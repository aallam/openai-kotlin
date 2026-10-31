package com.aallam.openai.client.internal.api

import com.aallam.openai.api.core.PaginatedList
import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.finetuning.FineTuningId
import com.aallam.openai.api.finetuning.FineTuningJob
import com.aallam.openai.api.finetuning.FineTuningJobEvent
import com.aallam.openai.api.finetuning.FineTuningRequest
import com.aallam.openai.api.finetuning.FineTuningJobDetails
import com.aallam.openai.api.finetuning.FineTuningCheckpoint
import com.aallam.openai.api.finetuning.FineTuningPage
import com.aallam.openai.api.exception.OpenAIAPIException
import com.aallam.openai.client.FineTuning
import com.aallam.openai.client.internal.extension.requestOptions
import com.aallam.openai.client.internal.http.HttpRequester
import com.aallam.openai.client.internal.http.perform
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

internal class FineTuningApi(private val requester: HttpRequester) : FineTuning {
    override suspend fun fineTuningJobDetails(id: FineTuningId, requestOptions: RequestOptions?): FineTuningJobDetails? = try {
        requester.perform {
            it.get {
                url(path = jobPath(id))
                requestOptions(requestOptions)
            }
        }
    } catch (error: OpenAIAPIException) {
        if (error.statusCode == HttpStatusCode.NotFound.value) null else throw error
    }

    override suspend fun pauseFineTuningJob(id: FineTuningId, requestOptions: RequestOptions?): FineTuningJobDetails =
        postJobOperation(id, "pause", requestOptions)

    override suspend fun resumeFineTuningJob(id: FineTuningId, requestOptions: RequestOptions?): FineTuningJobDetails =
        postJobOperation(id, "resume", requestOptions)

    override suspend fun fineTuningJobCheckpoints(
        id: FineTuningId,
        after: String?,
        limit: Int?,
        requestOptions: RequestOptions?,
    ): FineTuningPage<FineTuningCheckpoint> = requester.perform {
        it.get {
            url(path = "${jobPath(id)}/checkpoints") {
                after?.let { parameter("after", it) }
                limit?.let { parameter("limit", it) }
            }
            requestOptions(requestOptions)
        }
    }

    private suspend fun postJobOperation(id: FineTuningId, operation: String, options: RequestOptions?): FineTuningJobDetails =
        requester.perform {
            it.post {
                url(path = "${jobPath(id)}/$operation")
                requestOptions(options)
            }
        }

    private fun jobPath(id: FineTuningId): String {
        require(id.id.isNotEmpty() && id.id != "." && id.id != ".." && id.id.all { it.isLetterOrDigit() || it == '_' || it == '-' || it == '.' }) {
            "Fine-tuning job IDs must be single path segments"
        }
        return "${ApiPath.FineTuningJobs}/${id.id}"
    }

    override suspend fun fineTuningJob(request: FineTuningRequest, requestOptions: RequestOptions?): FineTuningJob {
        return requester.perform {
            it.post {
                url(path = ApiPath.FineTuningJobs)
                setBody(request)
                contentType(ContentType.Application.Json)
                requestOptions(requestOptions)
            }
        }
    }

    override suspend fun fineTuningJobs(
        after: String?,
        limit: Int?,
        requestOptions: RequestOptions?
    ): PaginatedList<FineTuningJob> {
        return requester.perform {
            it.get {
                url(path = ApiPath.FineTuningJobs) {
                    after?.let { value -> parameter("after", value) }
                    limit?.let { value -> parameter("limit", value) }
                }
                requestOptions(requestOptions)
            }
        }
    }

    override suspend fun fineTuningJob(id: FineTuningId, requestOptions: RequestOptions?): FineTuningJob? {
        val response = requester.perform<HttpResponse> {
            it.get {
                url(path = "${ApiPath.FineTuningJobs}/${id.id}")
                requestOptions(requestOptions)
            }
        }
        return if (response.status == HttpStatusCode.NotFound) null else response.body()
    }

    override suspend fun cancel(id: FineTuningId, requestOptions: RequestOptions?): FineTuningJob? {
        val response = requester.perform<HttpResponse> {
            it.post {
                url(path = "${ApiPath.FineTuningJobs}/${id.id}/cancel")
                requestOptions(requestOptions)
            }
        }
        return if (response.status == HttpStatusCode.NotFound) null else response.body()
    }

    override suspend fun fineTuningEvents(
        id: FineTuningId,
        after: String?,
        limit: Int?,
        requestOptions: RequestOptions?
    ): PaginatedList<FineTuningJobEvent> {
        return requester.perform {
            it.get {
                url(path = "${ApiPath.FineTuningJobs}/${id.id}/events") {
                    after?.let { value -> parameter("after", value) }
                    limit?.let { value -> parameter("limit", value) }
                }
                requestOptions(requestOptions)
            }
        }
    }
}
