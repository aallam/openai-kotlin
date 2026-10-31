package com.aallam.openai.api.admin

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

/** A page of administration resources with either ID cursors or a next cursor. */
@Serializable
public data class AdministrationPage<T>(
    /** Resources in this page. */
    public val data: List<T>,
    /** Whether another page is available. */
    @SerialName("has_more") public val hasMore: Boolean,
    /** Cursor supplied as after for the next page. */
    public val next: String? = null,
    /** First id. */
    @SerialName("first_id") public val firstId: String? = null,
    /** Last id. */
    @SerialName("last_id") public val lastId: String? = null,
    /** Object. */
    @SerialName("object") public val objectType: String? = null,
) : List<T> by data

/** Deletion result returned by an administration resource. */
@Serializable
public data class AdministrationDeletion(
    /** Deleted. */
    public val deleted: Boolean,
    /** Id. */
    public val id: String? = null,
    /** Object. */
    @SerialName("object") public val objectType: String? = null,
)
