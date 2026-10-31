package com.aallam.openai.sample.jvm

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
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import java.io.File
import java.util.Base64

/** Generates and edits PNG images, saves them locally, and deletes the uploaded edit input afterward. */
suspend fun images(openAI: OpenAI) {
    val model = ModelId(System.getenv("OPENAI_IMAGE_MODEL") ?: "gpt-image-2.5-sunburst")
    println("\n> Generate an image with ${model.id}...")
    val generated = openAI.image(
        ImageGenerationRequest(
            prompt = "A cute baby sea otter",
            model = model,
            n = 1,
            size = ImageSize.is1024x1024,
            quality = Quality.Low,
            outputFormat = ImageOutputFormat.Png,
        )
    )
    saveImages(generated, "generated")

    println("\n> Edit an uploaded image...")
    val input = openAI.file(
        FileUpload(
            file = FileSource(path = Resources.path("image.png")),
            purpose = Purpose("vision"),
        )
    )
    try {
        val edited = openAI.image(
            ImageEditRequest(
                images = listOf(ImageReference.file(input.id)),
                model = model,
                prompt = "Add a flamingo floating in the pool",
                n = 1,
                size = ImageSize.is1024x1024,
                quality = Quality.Low,
                outputFormat = ImageOutputFormat.Png,
            )
        )
        saveImages(edited, "edited")
    } finally {
        openAI.delete(input.id)
    }
}

private fun saveImages(response: ImageResponse, prefix: String) {
    println("Created ${response.data.size} image(s); size=${response.size?.size}, quality=${response.quality?.value}, tokens=${response.usage?.totalTokens}")
    response.data.forEachIndexed { index, image ->
        val file = File("$prefix-${index + 1}.png")
        file.writeBytes(Base64.getDecoder().decode(image.b64JSON))
        println("Saved ${file.absolutePath}")
    }
}
