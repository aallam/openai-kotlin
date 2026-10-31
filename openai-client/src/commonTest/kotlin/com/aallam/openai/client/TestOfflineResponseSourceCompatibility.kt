package com.aallam.openai.client

import com.aallam.openai.api.chat.ChatAnnotation
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.api.response.*
import kotlin.test.Test
import kotlin.test.assertEquals

/** Consumer-side inferred references exercise the original complete constructor/copy arities. */
class TestOfflineResponseSourceCompatibility {
    @Test
    fun responseReferencesKeepOriginalArity() {
        val make = ::Response
        val original = make(ResponseId("resp_legacy"), null, null, null, null, null, null, null, null, emptyList(), null, null, null, null, null, null, null, null, null, null, null, null, null, null)
        val copy = original::copy
        assertEquals(original, copy(ResponseId("resp_legacy"), null, null, null, null, null, null, null, null, emptyList(), null, null, null, null, null, null, null, null, null, null, null, null, null, null))
    }

    @Test
    fun responseOutputItemReferencesKeepOriginalArity() {
        val make = ::ResponseOutputItem
        val original = make(null, null, null, null, null, null, null, null, null)
        val copy = original::copy
        assertEquals(original, copy(null, null, null, null, null, null, null, null, null))
    }

    @Test
    fun responseOutputContentReferencesKeepOriginalArity() {
        val make = ::ResponseOutputContent
        val original = make(null, null, null, null, null, null)
        val copy = original::copy
        assertEquals(original, copy(null, null, null, null, null, null))
    }

    @Test
    fun responseInputItemReferencesKeepOriginalArity() {
        val make = ::ResponseInputItem
        val original = make(null, null, null, null, null)
        val copy = original::copy
        assertEquals(original, copy(null, null, null, null, null))
    }

    @Test
    fun responseRequestReferencesKeepOriginalArity() {
        val make = ::ResponseRequest
        val original = make(ModelId("model"), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null)
        val copy = original::copy
        assertEquals(original, copy(ModelId("model"), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null))
    }

    @Test
    fun responseReasoningReferencesKeepOriginalArity() {
        val make = ::ResponseReasoning
        val original = make(null)
        val copy = original::copy
        assertEquals(original, copy(null))
    }

    @Test
    fun responseTextReferencesKeepOriginalArity() {
        val make = ::ResponseText
        val original = make(null)
        val copy = original::copy
        assertEquals(original, copy(null))
    }

    @Test
    fun responseToolReferencesKeepOriginalArity() {
        val make = ::ResponseTool
        val original = make("legacy", null, null, null, null, null, null)
        val copy = original::copy
        assertEquals(original, copy("legacy", null, null, null, null, null, null))
    }

    @Test
    fun chatAnnotationReferencesKeepOriginalArity() {
        val make = ::ChatAnnotation
        val original = make("legacy", null, null, null, null, null)
        val copy = original::copy
        assertEquals(original, copy("legacy", null, null, null, null, null))
    }

}
