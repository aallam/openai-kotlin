package com.aallam.openai.client.internal.api

import com.aallam.openai.api.admin.*
import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.core.SortOrder
import com.aallam.openai.client.OrganizationSpending
import com.aallam.openai.client.internal.extension.requestOptions
import com.aallam.openai.client.internal.http.HttpRequester
import com.aallam.openai.client.internal.http.perform
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

internal class OrganizationSpendingApi(private val requester: HttpRequester) : OrganizationSpending {
    override suspend fun organizationSpendLimit(requestOptions: RequestOptions?): SpendLimit = get("organization/spend_limit", requestOptions)
    override suspend fun updateOrganizationSpendLimit(request: SpendLimitRequest, requestOptions: RequestOptions?): SpendLimit = post("organization/spend_limit", request, requestOptions)
    override suspend fun deleteOrganizationSpendLimit(requestOptions: RequestOptions?): SpendLimitDeleted = delete("organization/spend_limit", requestOptions)

    override suspend fun projectSpendLimit(projectId: String, requestOptions: RequestOptions?): SpendLimit = get("${projectPath(projectId)}/spend_limit", requestOptions)
    override suspend fun updateProjectSpendLimit(projectId: String, request: SpendLimitRequest, requestOptions: RequestOptions?): SpendLimit = post("${projectPath(projectId)}/spend_limit", request, requestOptions)
    override suspend fun deleteProjectSpendLimit(projectId: String, requestOptions: RequestOptions?): SpendLimitDeleted = delete("${projectPath(projectId)}/spend_limit", requestOptions)

    override suspend fun createOrganizationSpendAlert(request: SpendAlertRequest, requestOptions: RequestOptions?): SpendAlert = post("organization/spend_alerts", request, requestOptions)
    override suspend fun organizationSpendAlert(alertId: String, requestOptions: RequestOptions?): SpendAlert = get(alertPath("organization", alertId), requestOptions)
    override suspend fun updateOrganizationSpendAlert(alertId: String, request: SpendAlertRequest, requestOptions: RequestOptions?): SpendAlert = post(alertPath("organization", alertId), request, requestOptions)
    override suspend fun deleteOrganizationSpendAlert(alertId: String, requestOptions: RequestOptions?): SpendAlertDeleted = delete(alertPath("organization", alertId), requestOptions)
    override suspend fun organizationSpendAlerts(after: String?, before: String?, limit: Int?, order: SortOrder?, requestOptions: RequestOptions?): SpendAlertPage =
        alerts("organization/spend_alerts", after, before, limit, order, requestOptions)

    override suspend fun createProjectSpendAlert(projectId: String, request: SpendAlertRequest, requestOptions: RequestOptions?): SpendAlert = post("${projectPath(projectId)}/spend_alerts", request, requestOptions)
    override suspend fun projectSpendAlert(projectId: String, alertId: String, requestOptions: RequestOptions?): SpendAlert = get(alertPath(projectPath(projectId), alertId), requestOptions)
    override suspend fun updateProjectSpendAlert(projectId: String, alertId: String, request: SpendAlertRequest, requestOptions: RequestOptions?): SpendAlert = post(alertPath(projectPath(projectId), alertId), request, requestOptions)
    override suspend fun deleteProjectSpendAlert(projectId: String, alertId: String, requestOptions: RequestOptions?): SpendAlertDeleted = delete(alertPath(projectPath(projectId), alertId), requestOptions)
    override suspend fun projectSpendAlerts(projectId: String, after: String?, before: String?, limit: Int?, order: SortOrder?, requestOptions: RequestOptions?): SpendAlertPage =
        alerts("${projectPath(projectId)}/spend_alerts", after, before, limit, order, requestOptions)

    private suspend inline fun <reified T : Any> get(path: String, options: RequestOptions?): T = requester.perform {
        it.get { url(path = path); requestOptions(options) }.body()
    }

    private suspend inline fun <reified T : Any, reified B : Any> post(path: String, body: B, options: RequestOptions?): T = requester.perform {
        it.post {
            url(path = path)
            contentType(ContentType.Application.Json)
            setBody(body)
            requestOptions(options)
        }.body()
    }

    private suspend inline fun <reified T : Any> delete(path: String, options: RequestOptions?): T = requester.perform {
        it.delete { url(path = path); requestOptions(options) }.body()
    }

    private suspend fun alerts(path: String, after: String?, before: String?, limit: Int?, order: SortOrder?, options: RequestOptions?): SpendAlertPage = requester.perform {
        it.get {
            url(path = path) {
                after?.let { parameter("after", it) }
                before?.let { parameter("before", it) }
                limit?.let { parameter("limit", it) }
                order?.let { parameter("order", it.order) }
            }
            requestOptions(options)
        }.body()
    }

    private fun projectPath(projectId: String): String = "organization/projects/${segment(projectId)}"
    private fun alertPath(base: String, alertId: String): String = "$base/spend_alerts/${segment(alertId)}"
    private fun segment(id: String): String {
        require(id.isNotEmpty() && id != "." && id != ".." && id.none {
            it in "/\\?#%" || it.code < 32 || it.code in 127..159
        }) { "Invalid spend control resource ID" }
        return id
    }
}
