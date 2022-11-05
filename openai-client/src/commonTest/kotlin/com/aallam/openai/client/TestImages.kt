package com.aallam.openai.client

import com.aallam.openai.api.ExperimentalOpenAI
import com.aallam.openai.api.image.*
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.test.runTest
import okio.Path.Companion.toPath
import ulid.ULID
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalOpenAI::class)
class TestImages : TestOpenAI() {


    @Test
    fun imagesURL() = runTest {
        val request = ImageRequestURL(
            prompt = "A cute baby sea otter",
            n = 2,
            size = ImageSize.is1024x1024
        )
        val response = openAI.images(request)
        assertTrue { response.isNotEmpty() }
    }

    @Test
    fun imagesJSON() = runTest {
        val request = ImageRequestJSON(
            prompt = "A cute baby sea otter",
            n = 2,
            size = ImageSize.is1024x1024,
        )
        val response = openAI.images(request)
        assertTrue { response.isNotEmpty() }
        println(response)
    }

    @Test
    fun imagesEditURL() = runTest {
        val httpClient = HttpClient()
        val imagePath = writeImage(bytes = httpClient.get("https://i.imgur.com/mXFcDNB.png").body())
        val maskPath = writeImage(bytes = httpClient.get("https://i.imgur.com/D4MURbj.png").body())

        val request = ImageEditURL(
            image = imagePath,
            mask = maskPath,
            prompt = "a sunlit indoor lounge area with a pool containing a flamingo",
            n = 1,
            size = ImageSize.is1024x1024
        )
        val response = openAI.image(request)
        assertTrue { response.isNotEmpty() }
    }

    @Test
    fun imagesEditJSON() = runTest {
        val httpClient = HttpClient()
        val imagePath = writeImage(bytes = httpClient.get("https://i.imgur.com/mXFcDNB.png").body())
        val maskPath = writeImage(bytes = httpClient.get("https://i.imgur.com/D4MURbj.png").body())

        val request = ImageEditJSON(
            image = imagePath,
            mask = maskPath,
            prompt = "a sunlit indoor lounge area with a pool containing a flamingo",
            n = 1,
            size = ImageSize.is1024x1024
        )
        val response = openAI.image(request)
        assertTrue { response.isNotEmpty() }
    }

    private fun writeImage(bytes: ByteArray): String {
        val filename = "${ULID.randomULID()}.png"
        val filePath = filename.toPath()
        fileSystem.write(filePath) { write(bytes) }
        return filename
    }
}

