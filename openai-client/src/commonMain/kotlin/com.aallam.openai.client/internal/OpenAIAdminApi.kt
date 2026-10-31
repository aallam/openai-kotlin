package com.aallam.openai.client.internal

import com.aallam.openai.client.OpenAIAdmin
import com.aallam.openai.client.OrganizationUsage
import com.aallam.openai.client.internal.api.OrganizationUsageApi
import com.aallam.openai.client.internal.http.HttpRequester

internal class OpenAIAdminApi(requester: HttpRequester) : OpenAIAdmin,
    OrganizationUsage by OrganizationUsageApi(requester),
    AutoCloseable by requester
