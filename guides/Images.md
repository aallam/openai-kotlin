# Image generation and editing

Use a current GPT Image model explicitly for generation. The current API returns base64 images and does not accept the legacy `response_format` parameter.

```kotlin
val request = imageGeneration {
    model = ModelId("gpt-image-2.5-sunburst")
    prompt = "A watercolor landscape"
    background = ImageBackground.Transparent
    outputFormat = ImageOutputFormat.Png
    quality = Quality.High
}
val result = openAI.image(request)
val image = result.data.single().b64JSON
val usage = result.usage
```

For editing, reference one to sixteen images using uploaded file IDs, HTTPS URLs or data URLs. The service chooses its current edit model when `model` is omitted. Masks are optional; masks and markup cannot be combined.

```kotlin
val edit = ImageEditRequest(
    images = listOf(ImageReference.file(FileId("file_example"))),
    prompt = "Add a tree in the foreground",
)
val result = openAI.image(edit)
openAI.imageEvents(edit).collect { event ->
    when {
        event.type.endsWith(".partial_image") -> displayPreview(event.b64JSON)
        event.type.endsWith(".completed") -> displayFinal(event.b64JSON)
        event.type == "error" -> showError(event.message ?: event.error?.message)
    }
}
```

`imageEvents` starts a request on collection and closes its response when collection is cancelled. It emits partial, completed and service-error events; HTTP failures use the client's existing exception handling. `image()` preserves metadata and usage, while `imageJSON()` remains an image-list convenience for existing `ImageCreation` requests. The legacy `ImageEdit` model retains its required binary mask; `ImageEditRequest` is the additive current API with an optional mask. Legacy URL and variation operations remain for compatible providers; retired DALL-E models are not supported by OpenAI.
