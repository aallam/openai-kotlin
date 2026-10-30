package com.aallam.openai.api.misc

import com.aallam.openai.api.file.Purpose
import com.aallam.openai.api.upload.*
import kotlinx.serialization.json.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class TestUploads {
    @Test
    fun uploadUses64BitSizesAndFileExpiryWireShape() {
        val request = UploadRequest(8_000_000_000L, "large.bin", "application/octet-stream", Purpose("batch"), UploadExpirationPolicy(3600))
        val json = Json.encodeToJsonElement(UploadRequest.serializer(), request).jsonObject
        assertEquals(8_000_000_000L, json["bytes"]!!.jsonPrimitive.long)
        assertEquals(Json.parseToJsonElement("""{"seconds":3600,"anchor":"created_at"}"""), json["expires_after"])
        val encoded = Json { encodeDefaults = true }.encodeToJsonElement(UploadRequest.serializer(), request)
        assertEquals("created_at", encoded.jsonObject["expires_after"]!!.jsonObject["anchor"]!!.jsonPrimitive.content)
    }

    @Test
    fun completionKeepsOrderAndOmitsAbsentChecksum() {
        val request = UploadCompletionRequest(listOf(UploadPartId("second"), UploadPartId("first")))
        val encoded = Json.encodeToJsonElement(UploadCompletionRequest.serializer(), request).jsonObject
        assertEquals(Json.parseToJsonElement("""["second","first"]"""), encoded["part_ids"])
        assertFalse(encoded.containsKey("md5"))
    }

    @Test
    fun uploadedFilesPreserveLargeSizeAndExpiryWithoutChangingLegacyFile() {
        val payload = """{"id":"upload_1","bytes":8000000000,"created_at":1,"expires_at":3601,"filename":"large.bin","purpose":"batch","status":"completed","file":{"id":"file_1","bytes":8000000000,"created_at":2,"filename":"large.bin","purpose":"batch","expires_at":86402}}"""
        val upload = Json.decodeFromString(Upload.serializer(), payload)
        assertEquals(UploadStatus.Completed, upload.status)
        assertEquals(8_000_000_000L, upload.file!!.bytes)
        assertEquals(86402L, upload.file!!.expiresAt)
        assertEquals("file_1", upload.file!!.id.id)
    }
}
