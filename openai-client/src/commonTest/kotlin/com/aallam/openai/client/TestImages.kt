package com.aallam.openai.client

import com.aallam.openai.api.file.FileSource
import com.aallam.openai.api.image.*
import com.aallam.openai.client.internal.asSource
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

class TestImages : TestOpenAI() {

    private val httpClient = HttpClient()

    @Test
    fun imageCreationURL() = runTest {
        val request = imageCreation {
            prompt = "A cute baby sea otter"
            n = 2
            size = ImageSize.is256x256
        }
        val response = openAI.imageURL(request)
        assertTrue { response.isNotEmpty() }
    }

    @Test
    fun imageCreationJSON() = runTest {
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
    fun imageEditURL() = runTest {
        val imageBytes: ByteArray = httpClient.get("https://i.imgur.com/mXFcDNB.png").body()
        val maskBytes: ByteArray = httpClient.get("https://i.imgur.com/D4MURbj.png").body()
        val request = imageEdit {
            image = FileSource(name = "mXFcDNB.png", source = imageBytes.asSource())
            mask = FileSource(name = "D4MURbj.png", source = maskBytes.asSource())
            prompt = "a sunlit indoor lounge area with a pool containing a flamingo"
            n = 1
            size = ImageSize.is1024x1024
        }
        val response = openAI.imageURL(request)
        assertTrue { response.isNotEmpty() }
    }

    @Test
    fun imageEditJSON() = runTest {
        val imageBytes: ByteArray = httpClient.get("https://i.imgur.com/mXFcDNB.png").body()
        val maskBytes: ByteArray = httpClient.get("https://i.imgur.com/D4MURbj.png").body()
        val request = imageEdit {
            image = FileSource(name = "mXFcDNB.png", source = imageBytes.asSource())
            mask = FileSource(name = "D4MURbj.png", source = maskBytes.asSource())
            prompt = "a sunlit indoor lounge area with a pool containing a flamingo"
            n = 1
            size = ImageSize.is1024x1024
        }
        val response = openAI.imageJSON(request)
        assertTrue { response.isNotEmpty() }
    }

    @Test
    fun imageVariationURL() = runTest {
        val image: ByteArray = httpClient.get("https://i.imgur.com/iN0VFnF.png").body()
        val request = ImageVariation(
            image = FileSource("iN0VFnF.png", image.asSource()),
            n = 1,
            size = ImageSize.is1024x1024,
        )
        val response = openAI.imageURL(request)
        assertTrue { response.isNotEmpty() }
    }

    @Test
    fun imageVariationJSON() = runTest {
        val image: ByteArray = httpClient.get("https://i.imgur.com/iN0VFnF.png").body()
        val request = ImageVariation(
            image = FileSource("iN0VFnF.png", image.asSource()),
            n = 1,
            size = ImageSize.is1024x1024,
        )
        val response = openAI.imageJSON(request)
        assertTrue { response.isNotEmpty() }
    }
}
