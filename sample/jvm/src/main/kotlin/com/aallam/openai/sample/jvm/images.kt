package com.aallam.openai.sample.jvm

import com.aallam.openai.api.file.FileSource
import com.aallam.openai.api.image.*
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI

suspend fun images(openAI: OpenAI) {
    println("\n> Create images with GPT image model...")
    val images = openAI.imageCreate(
        creation = ImageCreation(
            prompt = "A cute baby sea otter",
            model = ModelId("gpt-image-1"),
            n = 2,
            size = ImageSize.is1024x1024,
            quality = Quality.Auto,
            background = Background.Auto,
            outputFormat = OutputFormat.PNG,
        )
    )
    println("Generated ${images.size} images (base64 JSON)")
    images.forEach { image ->
        println("Image preview: ${image.b64JSON.take(50)}...")
        image.revisedPrompt?.let { println("Revised prompt: $it") }
    }

    println("\n> Create images with DALL-E-3 (legacy)...")
    val dalleImages = openAI.imageURL(
        creation = ImageCreation(
            prompt = "A cute baby sea otter",
            model = ModelId("dall-e-3"),
            n = 1,
            size = ImageSize.is1024x1024,
            quality = Quality.HD,
            style = Style.Vivid,
        )
    )
    println("Generated ${dalleImages.size} images (URLs)")
    dalleImages.forEach { image ->
        println("URL: ${image.url}")
        image.revisedPrompt?.let { println("Revised prompt: $it") }
    }

    println("\n> Edit images...")
    val imageEdit = ImageEdit(
        image = FileSource(path = Resources.path("image.png")),
        mask = FileSource(path = Resources.path("image.png")),
        prompt = "a sunlit indoor lounge area with a pool containing a flamingo",
        n = 1,
        size = ImageSize.is1024x1024,
    )

    val imageEdits = openAI.imageEdit(imageEdit)
    println("Generated ${imageEdits.size} edited images (base64 JSON)")
}
