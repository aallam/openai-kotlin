package com.aallam.openai.client

import com.aallam.openai.api.ExperimentalOpenAI
import com.aallam.openai.api.image.ImageRequest
import com.aallam.openai.api.image.ImageSize
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

class TestImages: TestOpenAI() {

    @OptIn(ExperimentalOpenAI::class)
    @Test
    fun images() = runTest {
        val request = ImageRequest(
            prompt = "A cute baby sea otter",
            n = 2,
            size = ImageSize.is1024x1024
        )
        val response = openAI.images(request)
        assertTrue { response.isNotEmpty() }
        println(response)
    }
}
