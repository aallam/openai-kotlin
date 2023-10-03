package com.aallam.openai.client.internal.api

import com.aallam.openai.api.core.PaginatedList
import com.aallam.openai.api.finetuning.FineTuningId
import com.aallam.openai.api.finetuning.FineTuningJob
import com.aallam.openai.api.finetuning.FineTuningJobEvent
import com.aallam.openai.api.finetuning.FineTuningRequest
import com.aallam.openai.client.FineTuning
import com.aallam.openai.client.internal.http.HttpRequester
import com.aallam.openai.client.internal.http.perform
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

internal class FineTuningApi(private val requester: HttpRequester) : FineTuning {
    override suspend fun fineTuningJob(request: FineTuningRequest): FineTuningJob {
        return requester.perform {
            it.post {
                url(path = ApiPath.FineTuningJobs)
                setBody(request)
                contentType(ContentType.Application.Json)
            }
        }
    }

    override suspend fun fineTuningJobs(after: String?, limit: Int?): PaginatedList<FineTuningJob> {
        return requester.perform {
            it.get {
                url(path = ApiPath.FineTuningJobs) {
                    after?.let { value -> parameter("after", value) }
                    limit?.let { value -> parameter("limit", value) }
                }
            }
        }
    }

    override suspend fun fineTuningJob(id: FineTuningId): FineTuningJob? {
        val response = requester.perform<HttpResponse> {
            it.get { url(path = "${ApiPath.FineTuningJobs}/${id.id}") }
        }
        return if (response.status == HttpStatusCode.NotFound) null else response.body()
    }

    override suspend fun cancel(id: FineTuningId): FineTuningJob? {
        val response = requester.perform<HttpResponse> {
            it.post { url(path = "${ApiPath.FineTuningJobs}/${id.id}/cancel") }
        }
        return if (response.status == HttpStatusCode.NotFound) null else response.body()
    }

    override suspend fun fineTuningEvents(
        id: FineTuningId,
        after: String?,
        limit: Int?
    ): PaginatedList<FineTuningJobEvent> {
        return requester.perform {
            it.get {
                url(path = "${ApiPath.FineTuningJobs}/${id.id}/events") {
                    after?.let { value -> parameter("after", value) }
                    limit?.let { value -> parameter("limit", value) }
                }
            }
        }
    }
}
