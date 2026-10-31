package com.aallam.openai.client

import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.voice.*

/**
 * Manages saved custom voices and consent recordings in the authenticated project.
 * Default operations throw [UnsupportedOperationException], allowing existing custom clients to omit this capability.
 */
public interface VoiceManagement {
    /** Creates a prompt-generated or consented sample-based voice. Sample sources are consumed and closed on success or failure, without automatic retries. */
    public suspend fun createVoice(request: VoiceCreateRequest, requestOptions: RequestOptions? = null): VoiceResource =
        throw UnsupportedOperationException("Voice management is not supported by this client.")

    /** Deletes a voice; returns false when it does not exist. Its consent recording remains available. */
    public suspend fun deleteVoice(id: String, requestOptions: RequestOptions? = null): Boolean =
        throw UnsupportedOperationException("Voice management is not supported by this client.")

    /** Uploads a recording of the speaker providing the required consent phrase. Its source is consumed and closed on success or failure, without automatic retries. */
    public suspend fun createVoiceConsent(request: VoiceConsentRequest, requestOptions: RequestOptions? = null): VoiceConsent =
        throw UnsupportedOperationException("Voice management is not supported by this client.")

    /** Retrieves a consent recording's metadata, or null when it does not exist. */
    public suspend fun voiceConsent(id: String, requestOptions: RequestOptions? = null): VoiceConsent? =
        throw UnsupportedOperationException("Voice management is not supported by this client.")

    /** Changes only the recording's label; its audio and language are unchanged. */
    public suspend fun updateVoiceConsent(id: String, request: VoiceConsentUpdateRequest, requestOptions: RequestOptions? = null): VoiceConsent =
        throw UnsupportedOperationException("Voice management is not supported by this client.")

    /** Lists consent recordings; use the lastId cursor to retrieve another page. */
    public suspend fun voiceConsents(after: String? = null, limit: Int? = null, requestOptions: RequestOptions? = null): VoiceConsentPage =
        throw UnsupportedOperationException("Voice management is not supported by this client.")

    /** Deletes a consent recording; returns false when it does not exist. */
    public suspend fun deleteVoiceConsent(id: String, requestOptions: RequestOptions? = null): Boolean =
        throw UnsupportedOperationException("Voice management is not supported by this client.")
}
