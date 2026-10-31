package com.aallam.openai.client.internal

import com.aallam.openai.client.internal.api.FineTuningPermissionsApi
import com.aallam.openai.client.FineTuningPermissions
import com.aallam.openai.client.internal.api.OrganizationSpendingApi
import com.aallam.openai.client.OrganizationSpending
import com.aallam.openai.client.OpenAIAdmin
import com.aallam.openai.client.OrganizationAdministration
import com.aallam.openai.client.internal.api.OrganizationAdministrationApi
import com.aallam.openai.client.OrganizationGovernance
import com.aallam.openai.client.internal.api.OrganizationGovernanceApi
import com.aallam.openai.client.OrganizationUsage
import com.aallam.openai.client.internal.api.OrganizationUsageApi
import com.aallam.openai.client.internal.http.HttpRequester

internal class OpenAIAdminApi(requester: HttpRequester) : OpenAIAdmin,
    OrganizationUsage by OrganizationUsageApi(requester),
    OrganizationAdministration by OrganizationAdministrationApi(requester),
    OrganizationGovernance by OrganizationGovernanceApi(requester),
    OrganizationSpending by OrganizationSpendingApi(requester),
    FineTuningPermissions by FineTuningPermissionsApi(requester),
    AutoCloseable by requester
