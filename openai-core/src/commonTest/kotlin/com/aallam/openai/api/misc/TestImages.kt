package com.aallam.openai.api.misc

import com.aallam.openai.api.file.FileId
import com.aallam.openai.api.image.*
import com.aallam.openai.api.model.ModelId
import kotlinx.serialization.json.*
import kotlin.test.*

@OptIn(com.aallam.openai.api.BetaOpenAI::class)
class TestImages {
    @Test
    fun generationBuilderRetainsControlsWithoutLegacyFormat() {
        val request = imageGeneration {
            prompt = "A landscape"
            model = ModelId("gpt-image-2.5-sunburst")
            quality = Quality.High
            background = ImageBackground.Transparent
            outputCompression = 80
            outputFormat = ImageOutputFormat.Webp
            partialImages = 2
            safetyIdentifier = "visitor"
        }
        val body = Json.encodeToJsonElement(request).jsonObject
        assertEquals("high", body["quality"]?.jsonPrimitive?.content)
        assertEquals("transparent", body["background"]?.jsonPrimitive?.content)
        assertEquals(80, body["output_compression"]?.jsonPrimitive?.int)
        assertEquals(2, body["partial_images"]?.jsonPrimitive?.int)
        assertFalse("response_format" in body)
    }

    @Test
    fun editsUseReferencesAndOptionalMask() {
        val request = ImageEditRequest(
            images = listOf(ImageReference.file(FileId("file_1")), ImageReference.url("https://example.com/input.png")),
            prompt = "Combine these images",
            markup = ImageReference.url("data:image/png;base64,YQ=="),
            inputFidelity = ImageInputFidelity.High,
        )
        val body = Json.encodeToJsonElement(request).jsonObject
        assertEquals("file_1", body["images"]!!.jsonArray[0].jsonObject["file_id"]!!.jsonPrimitive.content)
        assertFalse("mask" in body)
        assertFalse("model" in body)
        assertFailsWith<IllegalArgumentException> {
            request.copy(mask = ImageReference.file(FileId("mask")))
        }
        assertFailsWith<IllegalArgumentException> { request.copy(images = emptyList()) }
    }

    @Test
    fun legacyConstructorsAndBuilderGettersRemainSourceCompatible() {
        val creation = ImageCreation("legacy prompt", 1, ImageSize.is1024x1024, null, ModelId("legacy-model"), Quality.HD, Style.Natural)
        val source = com.aallam.openai.api.file.FileSource("image.png", kotlinx.io.Buffer())
        val edit = ImageEdit(source, source, "legacy edit")
        val requiredMask: com.aallam.openai.api.file.FileSource = edit.mask
        val built = imageCreation {
            prompt = creation.prompt
            model = creation.model
            quality = Quality.HD
            style = Style.Natural
        }
        assertSame(source, requiredMask)
        assertEquals(creation.quality, built.quality)
        assertEquals(creation.style, built.style)
    }

    @Test
    fun metadataAndUsageSurviveDecoding() {
        val response = Json.decodeFromString<ImageResponse>("""{"created":1,"data":[{"b64_json":"YQ=="}],"quality":"high","size":"1536x1024","usage":{"input_tokens":10,"output_tokens":20,"total_tokens":30,"input_tokens_details":{"text_tokens":2,"image_tokens":8}}}""")
        assertEquals("YQ==", response.data.single().b64JSON)
        assertEquals(8, response.usage?.inputTokensDetails?.imageTokens)
        assertEquals(Quality.High, response.quality)
    }
}
