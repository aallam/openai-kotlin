package com.aallam.openai.client

import com.aallam.openai.api.file.FileSource
import com.aallam.openai.api.image.*
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.internal.TestFileSystem
import com.aallam.openai.client.internal.testFilePath
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class TestGptImages : TestOpenAI() {

    // -------------------- Image Creation Tests --------------------

    @Test
    fun imageCreateBasic() = test {
        val request = ImageCreation(
            prompt = "A cute baby sea otter",
            model = ModelId("gpt-image-1"),
            n = 1,
            size = ImageSize.is1024x1024
        )
        val response = openAI.imageCreate(request)
        assertTrue { response.isNotEmpty() }
        assertTrue { response.first().b64JSON.isNotEmpty() }
    }

    @Test
    fun imageCreateWithLowModeration() = test {
        val request = ImageCreation(
            prompt = "An artistic portrait",
            model = ModelId("gpt-image-1"),
            n = 1,
            size = ImageSize.is1024x1024,
            moderation = Moderation.Low
        )
        val response = openAI.imageCreate(request)
        assertTrue { response.isNotEmpty() }
        assertTrue { response.first().b64JSON.isNotEmpty() }

    }

    @Test
    fun imageCreateMultipleImages() = test {
        val request = ImageCreation(
            prompt = "A colorful butterfly",
            model = ModelId("gpt-image-1"),
            n = 2,
            size = ImageSize.is1024x1024
        )
        val response = openAI.imageCreate(request)
        assertTrue { response.size == 2 }
        assertTrue { response[0].b64JSON.isNotEmpty() }
        assertTrue { response[1].b64JSON.isNotEmpty() }
    }

    @Test
    fun imageCreateWithAllGptParameters() = test {
        val request = ImageCreation(
            prompt = "A futuristic city with flying cars",
            model = ModelId("gpt-image-1"),
            n = 1,
            size = ImageSize.is1024x1024,
            quality = Quality.High,
            background = Background.Opaque,
            moderation = Moderation.Auto,
            outputFormat = OutputFormat.PNG
        )
        val response = openAI.imageCreate(request)
        assertTrue { response.isNotEmpty() }
        assertTrue { response.first().b64JSON.isNotEmpty() }
    }

    @Test
    fun imageCreateGptImage15Basic() = test {
        val request = ImageCreation(
            prompt = "A cute puppy playing in the park",
            model = ModelId("gpt-image-1.5"),
            n = 1,
            size = ImageSize.is1024x1024
        )
        val response = openAI.imageCreate(request)
        assertTrue { response.isNotEmpty() }
        assertTrue { response.first().b64JSON.isNotEmpty() }
    }

    // -------------------- Image Edit Tests --------------------

    @Test
    fun imageEditBasic() = test {
        val request = ImageEdit(
            image = FileSource(path = testFilePath("image/pool.png"), fileSystem = TestFileSystem, contentType = "image/png"),
            mask = FileSource(path = testFilePath("image/poolmask.png"), fileSystem = TestFileSystem, contentType = "image/png"),
            prompt = "A sunlit indoor lounge area with a pool containing a flamingo",
            model = ModelId("gpt-image-1"),
            n = 1,
            size = ImageSize.is1024x1024
        )
        val response = openAI.imageEdit(request)
        assertTrue { response.isNotEmpty() }
        assertTrue { response.first().b64JSON.isNotEmpty() }
    }

    @Test
    fun imageEditWithBackground() = test {
        val request = ImageEdit(
            image = FileSource(path = testFilePath("image/pool.png"), fileSystem = TestFileSystem, contentType = "image/png"),
            mask = FileSource(path = testFilePath("image/poolmask.png"), fileSystem = TestFileSystem, contentType = "image/png"),
            prompt = "A pool with crystal clear water and palm trees",
            model = ModelId("gpt-image-1"),
            n = 1,
            size = ImageSize.is1024x1024,
            background = Background.Opaque
        )
        val response = openAI.imageEdit(request)
        assertTrue { response.isNotEmpty() }
        assertTrue { response.first().b64JSON.isNotEmpty() }

    }

    @Test
    fun imageEditWithAllGptParameters() = test {
        val request = ImageEdit(
            image = FileSource(path = testFilePath("image/pool.png"), fileSystem = TestFileSystem, contentType = "image/png"),
            mask = FileSource(path = testFilePath("image/poolmask.png"), fileSystem = TestFileSystem, contentType = "image/png"),
            prompt = "A luxurious resort-style pool with tropical decor",
            model = ModelId("gpt-image-1"),
            n = 1,
            size = ImageSize.is1024x1024,
            quality = Quality.High,
            background = Background.Opaque,
            inputFidelity = InputFidelity.High,
            outputFormat = OutputFormat.PNG
        )
        val response = openAI.imageEdit(request)
        assertTrue { response.isNotEmpty() }
        assertTrue { response.first().b64JSON.isNotEmpty() }
    }
}
