package com.aallam.openai.api.responses

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * Specifies the processing tier used for serving the request.
 */
@JvmInline
@Serializable
public value class ServiceTier(public val value: String) {
    public companion object {
        /** Use the project default service tier (usually "default"). */
        public val Auto: ServiceTier = ServiceTier("auto")
        /** Standard pricing and performance for the selected model. */
        public val Default: ServiceTier = ServiceTier("default")
        /** Flexible, lower-priority processing. */
        public val Flex: ServiceTier = ServiceTier("flex")
        /** Priority processing. */
        public val Priority: ServiceTier = ServiceTier("priority")
    }
}
