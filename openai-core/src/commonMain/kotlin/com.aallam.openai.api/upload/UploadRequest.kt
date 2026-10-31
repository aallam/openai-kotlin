package com.aallam.openai.api.upload

import com.aallam.openai.api.file.Purpose
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Creates an intermediate upload for a file of the declared total byte length. */
@Serializable
public data class UploadRequest(
    @SerialName("bytes") public val bytes: Long,
    @SerialName("filename") public val filename: String,
    @SerialName("mime_type") public val mimeType: String,
    @SerialName("purpose") public val purpose: Purpose,
    @SerialName("expires_after") public val expiresAfter: UploadExpirationPolicy? = null,
)

/** Expiration of the resulting file, distinct from the intermediate upload's own expiry. */
@Serializable
public data class UploadExpirationPolicy(
    /** Retention in seconds after the anchor. Current API accepts 3600 through 2592000 seconds. */
    @SerialName("seconds") public val seconds: Long,
    /** The supported anchor is created_at. */
    @EncodeDefault @SerialName("anchor") public val anchor: String = "created_at",
)

/** Completes an upload by assembling parts in exactly the supplied order, with an optional MD5 digest. */
@Serializable
public data class UploadCompletionRequest(
    @SerialName("part_ids") public val partIds: List<UploadPartId>,
    @SerialName("md5") public val md5: String? = null,
)
