package com.aallam.openai.client.internal.api

import com.aallam.openai.api.core.*
import com.aallam.openai.api.exception.OpenAIAPIException
import com.aallam.openai.api.webhook.*
import com.aallam.openai.client.Webhooks
import com.aallam.openai.client.internal.extension.requestOptions
import com.aallam.openai.client.internal.http.HttpRequester
import com.aallam.openai.client.internal.http.perform
import io.ktor.client.request.*
import io.ktor.http.*

internal class WebhooksApi(private val requester: HttpRequester) : Webhooks {
    override suspend fun webhookEndpoint(request: WebhookEndpointRequest, requestOptions: RequestOptions?): WebhookEndpoint =
        requester.perform {
            it.post {
                url(path = ApiPath.WebhookEndpoints)
                contentType(ContentType.Application.Json)
                setBody(request)
                requestOptions(requestOptions)
            }
        }

    override suspend fun webhookEndpoint(id: String, requestOptions: RequestOptions?): WebhookEndpoint? = try {
        requester.perform {
            it.get {
                url(path = "${ApiPath.WebhookEndpoints}/$id")
                requestOptions(requestOptions)
            }
        }
    } catch (error: OpenAIAPIException) {
        if (error.statusCode == 404) null else throw error
    }

    override suspend fun updateWebhookEndpoint(id: String, request: WebhookEndpointUpdate, requestOptions: RequestOptions?): WebhookEndpoint =
        requester.perform {
            it.post {
                url(path = "${ApiPath.WebhookEndpoints}/$id")
                contentType(ContentType.Application.Json)
                setBody(request)
                requestOptions(requestOptions)
            }
        }

    override suspend fun deleteWebhookEndpoint(id: String, requestOptions: RequestOptions?): Boolean = try {
        requester.perform<DeleteResponse> {
            it.delete {
                url(path = "${ApiPath.WebhookEndpoints}/$id")
                requestOptions(requestOptions)
            }
        }.deleted
    } catch (error: OpenAIAPIException) {
        if (error.statusCode == 404) false else throw error
    }

    override suspend fun webhookEndpoints(after: String?, limit: Int?, requestOptions: RequestOptions?): PaginatedList<WebhookEndpoint> =
        requester.perform {
            it.get {
                url(path = ApiPath.WebhookEndpoints) {
                    after?.let { parameter("after", it) }
                    limit?.let { parameter("limit", it) }
                }
                requestOptions(requestOptions)
            }
        }

    override suspend fun rotateWebhookSecret(id: String, request: WebhookSecretRotation, requestOptions: RequestOptions?): WebhookEndpoint =
        requester.perform {
            it.post {
                url(path = "${ApiPath.WebhookEndpoints}/$id/rotate_secret")
                contentType(ContentType.Application.Json)
                setBody(request)
                requestOptions(requestOptions)
            }
        }

    override suspend fun testWebhookEndpoint(id: String, request: WebhookTestRequest, requestOptions: RequestOptions?): WebhookTestResult =
        requester.perform {
            it.post {
                url(path = "${ApiPath.WebhookEndpoints}/$id/test")
                contentType(ContentType.Application.Json)
                setBody(request)
                requestOptions(requestOptions)
            }
        }
}
