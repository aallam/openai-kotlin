package com.aallam.openai.client

import com.aallam.openai.api.file.FileSource
import com.aallam.openai.api.image.ImageEdit
import com.aallam.openai.api.image.ImageSize
import com.aallam.openai.api.image.ImageVariation
import com.aallam.openai.api.image.imageCreation
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
        val image: ByteArray = httpClient.get("https://i.imgur.com/mXFcDNB.png").body()
        val mask: ByteArray = httpClient.get("https://i.imgur.com/D4MURbj.png").body()
        val request = ImageEdit(
            image = FileSource(name = "mXFcDNB.png", source = image.asSource()),
            mask = FileSource(name = "D4MURbj.png", source = mask.asSource()),
            prompt = "a sunlit indoor lounge area with a pool containing a flamingo",
            n = 1,
            size = ImageSize.is1024x1024
        )
        val response = openAI.imageURL(request)
        assertTrue { response.isNotEmpty() }
    }

    @Test
    fun imageEditJSON() = runTest {
        val image: ByteArray = httpClient.get("https://i.imgur.com/mXFcDNB.png").body()
        val mask: ByteArray = httpClient.get("https://i.imgur.com/D4MURbj.png").body()
        val request = ImageEdit(
            image = FileSource("mXFcDNB.png", image.asSource()),
            mask = FileSource("D4MURbj.png", mask.asSource()),
            prompt = "a sunlit indoor lounge area with a pool containing a flamingo",
            n = 1,
            size = ImageSize.is1024x1024
        )
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
