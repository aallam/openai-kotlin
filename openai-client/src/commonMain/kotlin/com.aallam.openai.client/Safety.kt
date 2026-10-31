package com.aallam.openai.client

import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.safety.SafetyAlert
import com.aallam.openai.api.safety.SafetyCase

/** Retrieves existing safety resources authorized for the supplied credential. */
public interface Safety {
    /** Retrieves a project safety alert. API errors, including missing resources, propagate. */
    public suspend fun safetyAlert(id: String, requestOptions: RequestOptions? = null): SafetyAlert =
        throw UnsupportedOperationException("Safety is not supported by this client.")

    /** Retrieves a safety case. API errors, including missing resources, propagate. */
    public suspend fun safetyCase(id: String, requestOptions: RequestOptions? = null): SafetyCase =
        throw UnsupportedOperationException("Safety is not supported by this client.")
}
