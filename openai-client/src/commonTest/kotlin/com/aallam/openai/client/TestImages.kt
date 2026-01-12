package com.aallam.openai.client

import com.aallam.openai.api.file.FileSource
import com.aallam.openai.api.image.ImageSize
import com.aallam.openai.api.image.imageCreation
import com.aallam.openai.api.image.imageEdit
import com.aallam.openai.api.image.imageVariation
import com.aallam.openai.client.internal.TestFileSystem
import com.aallam.openai.client.internal.testFilePath
import kotlin.test.Test
import kotlin.test.assertTrue

class TestImages : TestOpenAI() {

    @Test
    fun imageCreationURL() = test {
        val request = imageCreation {
            prompt = "A cute baby sea otter"
            n = 2
            size = ImageSize.is256x256
        }
        val response = openAI.imageURL(request)
        assertTrue { response.isNotEmpty() }
    }

    @Test
    fun imageCreationJSON() = test {
        val request = imageCreation {
            prompt = "A cute baby sea otter"
            n = 2
            size = ImageSize.is1024x1024
        }
        val response = openAI.imageJSON(request)
        assertTrue { response.isNotEmpty() }
        println(response)
    }

    @Test
    fun imageEditURL() = test {
        val request = imageEdit {
            image = FileSource(path = testFilePath("image/pool.png"), fileSystem = TestFileSystem, contentType = "image/png")
            mask = FileSource(path = testFilePath("image/poolmask.png"), fileSystem = TestFileSystem, contentType = "image/png")
            prompt = "a sunlit indoor lounge area with a pool containing a flamingo"
            n = 1
            size = ImageSize.is1024x1024
        }
        val response = openAI.imageURL(request)
        assertTrue { response.isNotEmpty() }
    }

    @Test
    fun imageEditJSON() = test {
        val request = imageEdit {
            image = FileSource(path = testFilePath("image/pool.png"), fileSystem = TestFileSystem, contentType = "image/png")
            mask = FileSource(path = testFilePath("image/poolmask.png"), fileSystem = TestFileSystem, contentType = "image/png")
            prompt = "a sunlit indoor lounge area with a pool containing a flamingo"
            n = 1
            size = ImageSize.is1024x1024
        }
        val response = openAI.imageJSON(request)
        assertTrue { response.isNotEmpty() }
    }

    @Test
    fun imageVariationURL() = test {
        val request = imageVariation {
            image = FileSource(path = testFilePath("image/pets.png"), fileSystem = TestFileSystem, contentType = "image/png")
            n = 1
            size = ImageSize.is1024x1024
        }
        val response = openAI.imageURL(request)
        assertTrue { response.isNotEmpty() }
    }

    @Test
    fun imageVariationJSON() = test {
        val request = imageVariation {
            image = FileSource(path = testFilePath("image/pets.png"), fileSystem = TestFileSystem, contentType = "image/png")
            n = 1
            size = ImageSize.is1024x1024
        }
        val response = openAI.imageJSON(request)
        assertTrue { response.isNotEmpty() }
    }
}
