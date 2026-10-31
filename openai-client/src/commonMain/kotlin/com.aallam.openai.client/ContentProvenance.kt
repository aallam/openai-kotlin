package com.aallam.openai.client

import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.file.FileSource
import com.aallam.openai.api.provenance.ContentProvenanceCheck

/** Checks image/audio uploads for supported OpenAI provenance signals. */
public interface ContentProvenance {
    /**
     * Consumes and closes the upload source, using its actual media type such as `image/png` or `audio/ogg`.
     * A not_detected result does not establish the asset's origin.
     * The source is closed even on failure; uploads are not automatically retried.
     */
    public suspend fun checkContentProvenance(file: FileSource, mediaType: String, requestOptions: RequestOptions? = null): ContentProvenanceCheck =
        throw UnsupportedOperationException("Content provenance is not supported by this client.")
}
