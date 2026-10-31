package com.aallam.openai.client.internal.api

import com.aallam.openai.api.admin.governance.*
import com.aallam.openai.api.core.PaginatedList
import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.exception.OpenAIAPIException
import com.aallam.openai.client.OrganizationGovernance
import com.aallam.openai.client.internal.extension.requestOptions
import com.aallam.openai.client.internal.http.HttpRequester
import com.aallam.openai.client.internal.http.perform
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

internal class OrganizationGovernanceApi(private val requester: HttpRequester) : OrganizationGovernance {
    override suspend fun auditLogs(query: AuditLogQuery, requestOptions: RequestOptions?): PaginatedList<AuditLog> =
        get("organization/audit_logs", requestOptions) { auditQuery(query) }

    override suspend fun uploadOrganizationCertificate(request: CertificateUploadRequest, requestOptions: RequestOptions?): OrganizationCertificate =
        post("organization/certificates", request, requestOptions)

    override suspend fun organizationCertificate(id: String, include: List<String>?, requestOptions: RequestOptions?): OrganizationCertificate? =
        getOrNull("organization/certificates/${segment(id)}", requestOptions) { include?.forEach { parameters.append("include[]", it) } }

    override suspend fun updateOrganizationCertificate(id: String, request: CertificateUpdateRequest, requestOptions: RequestOptions?): OrganizationCertificate =
        post("organization/certificates/${segment(id)}", request, requestOptions)

    override suspend fun deleteOrganizationCertificate(id: String, requestOptions: RequestOptions?): CertificateDeletion =
        delete("organization/certificates/${segment(id)}", requestOptions)

    override suspend fun organizationCertificates(query: CertificateListQuery, requestOptions: RequestOptions?): PaginatedList<OrganizationCertificate> =
        get("organization/certificates", requestOptions) { certificateQuery(query) }

    override suspend fun activateOrganizationCertificates(request: CertificateActivationRequest, requestOptions: RequestOptions?): CertificateActivationResult =
        post("organization/certificates/activate", request, requestOptions)

    override suspend fun deactivateOrganizationCertificates(request: CertificateActivationRequest, requestOptions: RequestOptions?): CertificateActivationResult =
        post("organization/certificates/deactivate", request, requestOptions)

    override suspend fun projectCertificates(projectId: String, query: CertificateListQuery, requestOptions: RequestOptions?): PaginatedList<OrganizationCertificate> =
        get("${project(projectId)}/certificates", requestOptions) { certificateQuery(query) }

    override suspend fun activateProjectCertificates(projectId: String, request: CertificateActivationRequest, requestOptions: RequestOptions?): CertificateActivationResult =
        post("${project(projectId)}/certificates/activate", request, requestOptions)

    override suspend fun deactivateProjectCertificates(projectId: String, request: CertificateActivationRequest, requestOptions: RequestOptions?): CertificateActivationResult =
        post("${project(projectId)}/certificates/deactivate", request, requestOptions)

    override suspend fun organizationDataRetention(requestOptions: RequestOptions?): DataRetentionSetting =
        get("organization/data_retention", requestOptions)

    override suspend fun updateOrganizationDataRetention(request: OrganizationRetentionRequest, requestOptions: RequestOptions?): DataRetentionSetting =
        post("organization/data_retention", request, requestOptions)

    override suspend fun projectDataRetention(projectId: String, requestOptions: RequestOptions?): DataRetentionSetting =
        get("${project(projectId)}/data_retention", requestOptions)

    override suspend fun updateProjectDataRetention(projectId: String, request: ProjectRetentionRequest, requestOptions: RequestOptions?): DataRetentionSetting =
        post("${project(projectId)}/data_retention", request, requestOptions)

    override suspend fun registerExternalKey(request: ExternalKeyRequest, requestOptions: RequestOptions?): ExternalKey =
        post("organization/external_keys", request, requestOptions)

    override suspend fun externalKey(id: String, requestOptions: RequestOptions?): ExternalKey? =
        getOrNull("organization/external_keys/${segment(id)}", requestOptions)

    override suspend fun externalKeys(query: ExternalKeyListQuery, requestOptions: RequestOptions?): PaginatedList<ExternalKey> =
        get("organization/external_keys", requestOptions) { query.after?.let { parameters.append("after", it) }; query.limit?.let { parameters.append("limit", it.toString()) }; query.order?.let { parameters.append("order", it.order) }; query.purpose?.let { parameters.append("purpose", it) } }

    override suspend fun deleteExternalKey(id: String, requestOptions: RequestOptions?): ExternalKey =
        delete("organization/external_keys/${segment(id)}", requestOptions)

    override suspend fun validateExternalKey(id: String, requestOptions: RequestOptions?): ExternalKeyValidation =
        postEmpty("organization/external_keys/${segment(id)}/validate", requestOptions)

    override suspend fun registerExternalStorage(request: ExternalStorageRequest, requestOptions: RequestOptions?): ExternalStorageConfiguration =
        post("organization/external_storage", request, requestOptions)

    override suspend fun externalStorage(id: String, requestOptions: RequestOptions?): ExternalStorageConfiguration? =
        getOrNull("organization/external_storage/${segment(id)}", requestOptions)

    override suspend fun externalStorageConfigurations(query: ExternalStorageListQuery, requestOptions: RequestOptions?): PaginatedList<ExternalStorageConfiguration> =
        get("organization/external_storage", requestOptions) { query.after?.let { parameters.append("after", it) }; query.limit?.let { parameters.append("limit", it.toString()) }; query.order?.let { parameters.append("order", it.order) }; query.projectId?.let { parameters.append("project_id", it) } }

    override suspend fun deleteExternalStorage(id: String, requestOptions: RequestOptions?): ExternalStorageDeletion =
        delete("organization/external_storage/${segment(id)}", requestOptions)

    override suspend fun validateExternalStorage(id: String, requestOptions: RequestOptions?): ExternalStorageConfiguration =
        postEmpty("organization/external_storage/${segment(id)}/validate", requestOptions)

    override suspend fun projectModelPermissions(projectId: String, requestOptions: RequestOptions?): ProjectModelPermissions? =
        getOrNull("${project(projectId)}/model_permissions", requestOptions)

    override suspend fun updateProjectModelPermissions(projectId: String, request: ProjectModelPermissionsRequest, requestOptions: RequestOptions?): ProjectModelPermissions =
        post("${project(projectId)}/model_permissions", request, requestOptions)

    override suspend fun deleteProjectModelPermissions(projectId: String, requestOptions: RequestOptions?): ProjectModelPermissionsDeletion =
        delete("${project(projectId)}/model_permissions", requestOptions)

    override suspend fun projectHostedToolPermissions(projectId: String, requestOptions: RequestOptions?): ProjectHostedToolPermissions =
        get("${project(projectId)}/hosted_tool_permissions", requestOptions)

    override suspend fun updateProjectHostedToolPermissions(projectId: String, request: ProjectHostedToolPermissionsRequest, requestOptions: RequestOptions?): ProjectHostedToolPermissions =
        post("${project(projectId)}/hosted_tool_permissions", request, requestOptions)

    override suspend fun projectRateLimits(projectId: String, query: ProjectRateLimitQuery, requestOptions: RequestOptions?): PaginatedList<ProjectRateLimit> =
        get("${project(projectId)}/rate_limits", requestOptions) { query.after?.let { parameters.append("after", it) }; query.before?.let { parameters.append("before", it) }; query.limit?.let { parameters.append("limit", it.toString()) } }

    override suspend fun updateProjectRateLimit(projectId: String, id: String, request: ProjectRateLimitRequest, requestOptions: RequestOptions?): ProjectRateLimit =
        post("${project(projectId)}/rate_limits/${segment(id)}", request, requestOptions)

    private suspend inline fun <reified R : Any> get(path: String, options: RequestOptions?, crossinline query: URLBuilder.() -> Unit = {}): R = requester.perform {
        it.get { url(path = path) { query() }; requestOptions(options) }.body()
    }

    private suspend inline fun <reified R : Any> getOrNull(path: String, options: RequestOptions?, crossinline query: URLBuilder.() -> Unit = {}): R? = try {
        get<R>(path, options, query)
    } catch (error: OpenAIAPIException) {
        if (error.statusCode == HttpStatusCode.NotFound.value) null else throw error
    }

    private suspend inline fun <reified R : Any, reified B : Any> post(path: String, body: B, options: RequestOptions?): R = requester.perform {
        it.post { url(path = path); contentType(ContentType.Application.Json); setBody(body); requestOptions(options) }.body()
    }

    private suspend inline fun <reified R : Any> postEmpty(path: String, options: RequestOptions?): R = requester.perform {
        it.post { url(path = path); requestOptions(options) }.body()
    }

    private suspend inline fun <reified R : Any> delete(path: String, options: RequestOptions?): R = requester.perform {
        it.delete { url(path = path); requestOptions(options) }.body()
    }

    private fun project(id: String): String = "organization/projects/${segment(id)}"

    private fun segment(id: String): String {
        require(id.isNotEmpty() && id != "." && id != ".." && id.none { it in "/\\?#%" || it.isISOControl() }) { "Invalid resource identifier" }
        return id
    }

    private fun URLBuilder.certificateQuery(query: CertificateListQuery) {
        query.after?.let { parameters.append("after", it) }
        query.limit?.let { parameters.append("limit", it.toString()) }
        query.order?.let { parameters.append("order", it.order) }
    }

    private fun URLBuilder.auditQuery(query: AuditLogQuery) {
        query.actorEmails?.forEach { parameters.append("actor_emails[]", it) }
        query.actorIds?.forEach { parameters.append("actor_ids[]", it) }
        query.after?.let { parameters.append("after", it) }
        query.before?.let { parameters.append("before", it) }
        query.effectiveAt?.let { range ->
            range.gt?.let { parameters.append("effective_at[gt]", it.toString()) }
            range.gte?.let { parameters.append("effective_at[gte]", it.toString()) }
            range.lt?.let { parameters.append("effective_at[lt]", it.toString()) }
            range.lte?.let { parameters.append("effective_at[lte]", it.toString()) }
        }
        query.eventTypes?.forEach { parameters.append("event_types[]", it) }
        query.limit?.let { parameters.append("limit", it.toString()) }
        query.projectIds?.forEach { parameters.append("project_ids[]", it) }
        query.resourceIds?.forEach { parameters.append("resource_ids[]", it) }
        query.tenantOnly?.let { parameters.append("tenant_only", it.toString()) }
    }
}
