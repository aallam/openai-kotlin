package com.aallam.openai.client

import com.aallam.openai.api.core.PaginatedList
import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.webhook.*

/** Manages webhook endpoints in the authenticated project. */
public interface Webhooks {
    /**
     * Create an endpoint; signingSecret is returned only here and during rotation.
     * @throws UnsupportedOperationException when a custom client has not implemented this operation.
     */
    public suspend fun webhookEndpoint(request: WebhookEndpointRequest, requestOptions: RequestOptions? = null): WebhookEndpoint =
        throw UnsupportedOperationException("Webhook endpoint operation is not implemented")

    /**
     * Retrieve endpoint configuration without returning its signing key.
     * @throws UnsupportedOperationException when a custom client has not implemented this operation.
     */
    public suspend fun webhookEndpoint(id: String, requestOptions: RequestOptions? = null): WebhookEndpoint? =
        throw UnsupportedOperationException("Webhook endpoint operation is not implemented")

    /**
     * Update an endpoint configuration.
     * @throws UnsupportedOperationException when a custom client has not implemented this operation.
     */
    public suspend fun updateWebhookEndpoint(id: String, request: WebhookEndpointUpdate, requestOptions: RequestOptions? = null): WebhookEndpoint =
        throw UnsupportedOperationException("Webhook endpoint operation is not implemented")

    /**
     * Delete an endpoint; returns false when it no longer exists.
     * @throws UnsupportedOperationException when a custom client has not implemented this operation.
     */
    public suspend fun deleteWebhookEndpoint(id: String, requestOptions: RequestOptions? = null): Boolean =
        throw UnsupportedOperationException("Webhook endpoint operation is not implemented")

    /**
     * List project endpoints with cursor metadata.
     * @throws UnsupportedOperationException when a custom client has not implemented this operation.
     */
    public suspend fun webhookEndpoints(after: String? = null, limit: Int? = null, requestOptions: RequestOptions? = null): PaginatedList<WebhookEndpoint> =
        throw UnsupportedOperationException("Webhook endpoint operation is not implemented")

    /**
     * Rotate a signing key, optionally retaining the old key for 24 hours.
     * @throws UnsupportedOperationException when a custom client has not implemented this operation.
     */
    public suspend fun rotateWebhookSecret(id: String, request: WebhookSecretRotation = WebhookSecretRotation(), requestOptions: RequestOptions? = null): WebhookEndpoint =
        throw UnsupportedOperationException("Webhook endpoint operation is not implemented")

    /**
     * Send a sample event; inspect the returned statusCode for receiver success.
     * @throws UnsupportedOperationException when a custom client has not implemented this operation.
     */
    public suspend fun testWebhookEndpoint(id: String, request: WebhookTestRequest, requestOptions: RequestOptions? = null): WebhookTestResult =
        throw UnsupportedOperationException("Webhook endpoint operation is not implemented")

}
