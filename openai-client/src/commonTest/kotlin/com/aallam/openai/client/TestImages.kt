package com.aallam.openai.client

import com.aallam.openai.api.file.FileSource
import com.aallam.openai.api.file.FileUpload
import com.aallam.openai.api.file.Purpose
import com.aallam.openai.api.image.ImageEditRequest
import com.aallam.openai.api.image.ImageGenerationRequest
import com.aallam.openai.api.image.ImageOutputFormat
import com.aallam.openai.api.image.ImageReference
import com.aallam.openai.api.image.ImageResponse
import com.aallam.openai.api.image.ImageSize
import com.aallam.openai.api.image.Quality
import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.internal.TestFileSystem
import com.aallam.openai.client.internal.env
import com.aallam.openai.client.internal.testFilePath
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TestImages : TestOpenAI() {
    private val imageModel: ModelId
        get() = ModelId(env("OPENAI_IMAGE_MODEL") ?: "gpt-image-2.5-sunburst")

    @Test
    fun generateCurrentImage() = test {
        imageClient().use { client ->
            val response = client.image(
                ImageGenerationRequest(
                    prompt = "A cute baby sea otter",
                    model = imageModel,
                    n = 1,
                    size = ImageSize.is1024x1024,
                    quality = Quality.Low,
                    outputFormat = ImageOutputFormat.Png,
                )
            )
            assertSingleImage(response)
        }
    }

    @Test
    fun editUploadedImageWithoutMask() = test {
        imageClient().use { client ->
            val input = client.file(
                FileUpload(
                    file = FileSource(path = testFilePath("image/pool.png"), fileSystem = TestFileSystem),
                    purpose = Purpose("vision"),
                )
            )
            try {
                val response = client.image(
                    ImageEditRequest(
                        images = listOf(ImageReference.file(input.id)),
                        model = imageModel,
                        prompt = "Add a flamingo floating in the pool",
                        n = 1,
                        size = ImageSize.is1024x1024,
                        quality = Quality.Low,
                        outputFormat = ImageOutputFormat.Png,
                    )
                )
                assertSingleImage(response)
            } finally {
                assertTrue(client.delete(input.id), "Uploaded input should be deleted")
            }
        }
    }

    private fun imageClient(): OpenAI {
        val config = openAIConfig()
        return OpenAI(
            OpenAIConfig(
                token = config.token,
                logging = LoggingConfig(LogLevel.Info),
                timeout = config.timeout,
                retry = config.retry,
            )
        )
    }

    private fun assertSingleImage(response: ImageResponse) {
        assertTrue(response.created > 0)
        assertEquals(1, response.data.size)
        assertTrue(response.data.single().b64JSON.isNotBlank())
    }
}
