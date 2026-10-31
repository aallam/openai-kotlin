package com.aallam.openai.client

import com.aallam.openai.client.internal.OpenAIAdminApi
import com.aallam.openai.client.internal.createHttpClient
import com.aallam.openai.client.internal.http.HttpTransport

/** Organization management client. Use an Admin API key, separately from application credentials. */
public interface OpenAIAdmin : OrganizationUsage, OrganizationAdministration, OrganizationGovernance, OrganizationSpending, FineTuningPermissions, AutoCloseable

/** Creates an administration client with the supplied Admin API key. */
public fun OpenAIAdmin(token: String): OpenAIAdmin = OpenAIAdmin(OpenAIConfig(token = token))

/** Creates an administration client; [OpenAIConfig.token] must authorize the requested admin operations. */
public fun OpenAIAdmin(config: OpenAIConfig): OpenAIAdmin = OpenAIAdminApi(HttpTransport(createHttpClient(config)))
