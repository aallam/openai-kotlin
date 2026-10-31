import com.aallam.openai.api.audio.TranscriptionRequest
import com.aallam.openai.api.audio.TranslationRequest
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.file.FileSource
import com.aallam.openai.api.file.FileUpload
import com.aallam.openai.api.file.Purpose
import com.aallam.openai.api.image.ImageGenerationRequest
import com.aallam.openai.api.image.ImageEditRequest
import com.aallam.openai.api.image.ImageReference
import com.aallam.openai.api.image.ImageResponse
import com.aallam.openai.api.image.ImageOutputFormat
import com.aallam.openai.api.image.Quality
import com.aallam.openai.api.image.ImageSize
import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.api.moderation.ModerationRequest
import com.aallam.openai.client.LoggingConfig
import com.aallam.openai.client.OpenAI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.io.files.Path
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.coroutines.coroutineContext

suspend fun main() {
    val apiKey = js("process.env.OPENAI_API_KEY").unsafeCast<String?>()
    val token = requireNotNull(apiKey) { "OPENAI_API_KEY environment variable must be set." }
    val configuredModel = js("process.env.OPENAI_MODEL").unsafeCast<String?>()
    val model = ModelId(requireNotNull(configuredModel) { "OPENAI_MODEL must name a supported Chat model." })
    val configuredImageModel = js("process.env.OPENAI_IMAGE_MODEL").unsafeCast<String?>()
    val imageModel = ModelId(configuredImageModel ?: "gpt-image-2.5-sunburst")
    val openAI = OpenAI(token = token, logging = LoggingConfig(LogLevel.Info))
    val scope = CoroutineScope(coroutineContext)

    println("> Getting available models...")
    openAI.models().forEach(::println)

    println("\n> Getting model...")
    val selectedModel = openAI.model(modelId = model)
    println(selectedModel)

    println("\n> Read files...")
    val files = openAI.files()
    println(files)

    println("\n> Create moderations...")
    val moderation = openAI.moderations(
        request = ModerationRequest(
            input = listOf("I want to kill them.")
        )
    )
    println(moderation)

    println("\n> Generate an image...")
    val generated = openAI.image(
        ImageGenerationRequest(
            prompt = "A cute baby sea otter",
            model = imageModel,
            n = 1,
            size = ImageSize.is1024x1024,
            quality = Quality.Low,
            outputFormat = ImageOutputFormat.Png,
        )
    )
    printImageMetadata(generated)

    println("\n> Edit an uploaded image...")
    val input = openAI.file(
        FileUpload(
            file = FileSource(path = Path("kotlin", "image.png")),
            purpose = Purpose("vision"),
        )
    )
    try {
        val edited = openAI.image(
            ImageEditRequest(
                images = listOf(ImageReference.file(input.id)),
                model = imageModel,
                prompt = "Add a flamingo floating in the pool",
                n = 1,
                size = ImageSize.is1024x1024,
                quality = Quality.Low,
                outputFormat = ImageOutputFormat.Png,
            )
        )
        printImageMetadata(edited)
    } finally {
        openAI.delete(input.id)
    }

    println("\n> Create chat completions...")
    val chatCompletionRequest = ChatCompletionRequest(
        model = model,
        messages = listOf(
            ChatMessage(
                role = ChatRole.System,
                content = "You are a helpful assistant that translates English to French."
            ),
            ChatMessage(
                role = ChatRole.User,
                content = "Translate the following English text to French: “OpenAI is awesome!”"
            )
        )
    )
    openAI.chatCompletion(chatCompletionRequest).choices.forEach(::println)

    println("\n>️ Creating chat completions stream...")
    openAI.chatCompletions(chatCompletionRequest)
        .onEach { print(it.choices.first().delta?.content.orEmpty()) }
        .onCompletion { println() }
        .launchIn(scope)
        .join()

    println("\n>️ Create transcription...")
    val transcriptionRequest = TranscriptionRequest(
        audio = FileSource(path = Path("kotlin","micro-machines.wav")),
        model = ModelId("whisper-1"),
    )
    val transcription = openAI.transcription(transcriptionRequest)
    println(transcription)

    println("\n> Legacy audio translation (scheduled retirement: 2027-02-26)...")
    val translationRequest = TranslationRequest(
        audio = FileSource(path = Path("kotlin", "multilingual.wav")),
        model = ModelId("whisper-1"),
    )
    val translation = openAI.translation(translationRequest)
    println(translation)
}

@OptIn(ExperimentalEncodingApi::class)
private fun printImageMetadata(response: ImageResponse) {
    println("Created ${response.data.size} image(s); size=${response.size?.size}, tokens=${response.usage?.totalTokens}")
    response.data.forEachIndexed { index, image ->
        val bytes = Base64.decode(image.b64JSON)
        println("Image ${index + 1}: ${bytes.size} decoded PNG bytes")
    }
}
