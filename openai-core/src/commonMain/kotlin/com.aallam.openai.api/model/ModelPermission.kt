package com.aallam.openai.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Model permission details.
 */
@Serializable
public data class ModelPermission(
    @SerialName("id") public val id: String,
    @SerialName("created") public val created: Long,
    @SerialName("allow_create_engine") public val allowCreateEngine: Boolean,
    @SerialName("allow_sampling") public val allowSampling: Boolean,
    @SerialName("allow_logprobs") public val allowLogprobs: Boolean,
    @SerialName("allow_search_indices") public val allowSearchIndices: Boolean,
    @SerialName("allow_view") public val allowView: Boolean,
    @SerialName("allow_fine_tuning") public val allowFineTuning: Boolean,
    @SerialName("organization") public val organization: String,
    @SerialName("is_blocking") public val isBlocking: Boolean,
)
