package com.aallam.openai.client.internal.api

import com.aallam.openai.client.internal.SingleUseRequest
import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.file.FileSource
import com.aallam.openai.api.provenance.ContentProvenanceCheck
import com.aallam.openai.client.ContentProvenance
import com.aallam.openai.client.internal.extension.appendFileSource
import com.aallam.openai.client.internal.extension.requestOptions
import com.aallam.openai.client.internal.http.HttpRequester
import com.aallam.openai.client.internal.http.perform
import io.ktor.client.request.forms.*
import io.ktor.client.plugins.retry
import io.ktor.http.ContentType
import kotlinx.io.buffered

internal class ContentProvenanceApi(private val requester: HttpRequester) : ContentProvenance {
    override suspend fun checkContentProvenance(file: FileSource, mediaType: String, requestOptions: RequestOptions?): ContentProvenanceCheck =
        file.source.buffered().use { source ->
            requester.perform {
                it.submitFormWithBinaryData(url = "content_provenance_checks", formData = formData {
                    appendFileSource("file", FileSource(file.name, source), ContentType.parse(mediaType))
                }) {
                    attributes.put(SingleUseRequest, Unit)
                    retry { maxRetries = 0 }
                    requestOptions(requestOptions)
                }
            }
        }
}
