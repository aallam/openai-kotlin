package com.aallam.openai.client

import com.aallam.openai.api.file.FileSource
import com.aallam.openai.api.image.ImageSize
import com.aallam.openai.api.image.imageCreation
import com.aallam.openai.api.image.imageEdit
import com.aallam.openai.api.image.imageVariation
import com.aallam.openai.client.internal.asSource
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.minutes

class TestImages : TestOpenAI() {

    private val httpClient = HttpClient()

    @Test
    fun imageCreationURL() = runTest(timeout = 1.minutes) {
        val request = imageCreation {
            prompt = "A cute baby sea otter"
            n = 2
            size = ImageSize.is256x256
        }
        val response = openAI.imageURL(request)
        assertTrue { response.isNotEmpty() }
    }

    @Test
    fun imageCreationJSON() = runTest(timeout = 1.minutes) {
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
    fun imageEditURL() = runTest(timeout = 1.minutes) {
        val imageBytes: ByteArray = httpClient.get(PoolImage).body()
        val maskBytes: ByteArray = httpClient.get(PoolMaskImage).body()
        val request = imageEdit {
            image = FileSource(name = "pool.png", source = imageBytes.asSource())
            mask = FileSource(name = "poolmask.png", source = maskBytes.asSource())
            prompt = "a sunlit indoor lounge area with a pool containing a flamingo"
            n = 1
            size = ImageSize.is1024x1024
        }
        val response = openAI.imageURL(request)
        assertTrue { response.isNotEmpty() }
    }

    @Test
    fun imageEditJSON() = runTest(timeout = 1.minutes) {
        val imageBytes: ByteArray = httpClient.get(PoolImage).body()
        val maskBytes: ByteArray = httpClient.get(PoolMaskImage).body()
        val request = imageEdit {
            image = FileSource(name = "pool.png", source = imageBytes.asSource())
            mask = FileSource(name = "poolmask.png", source = maskBytes.asSource())
            prompt = "a sunlit indoor lounge area with a pool containing a flamingo"
            n = 1
            size = ImageSize.is1024x1024
        }
        val response = openAI.imageJSON(request)
        assertTrue { response.isNotEmpty() }
    }

    @Test
    fun imageVariationURL() = runTest(timeout = 1.minutes) {
        val imageBytes: ByteArray = httpClient.get(PetsImage).body()
        val request = imageVariation {
            image = FileSource("pets.png", imageBytes.asSource())
            n = 1
            size = ImageSize.is1024x1024
        }
        val response = openAI.imageURL(request)
        assertTrue { response.isNotEmpty() }
    }

    @Test
    fun imageVariationJSON() = runTest(timeout = 1.minutes) {
        val imageBytes: ByteArray = httpClient.get(PetsImage).body()
        val request = imageVariation {
            image = FileSource("pets.png", imageBytes.asSource())
            n = 1
            size = ImageSize.is1024x1024
        }
        val response = openAI.imageJSON(request)
        assertTrue { response.isNotEmpty() }
    }


    companion object {
        private val Path = "https://raw.githubusercontent.com/aallam/sample-data/main/openai/image"
        private val PoolImage = "$Path/pool.png"
        private val PoolMaskImage = "$Path/poolmask.png"
        private val PetsImage = "$Path/pets.png"
    }
}
