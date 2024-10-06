import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.audio.TranscriptionRequest
import com.aallam.openai.api.audio.TranslationRequest
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.completion.CompletionRequest
import com.aallam.openai.api.file.FileSource
import com.aallam.openai.api.image.ImageCreation
import com.aallam.openai.api.image.ImageEdit
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
import kotlin.coroutines.coroutineContext

suspend fun main() {
    val apiKey = js("process.env.OPENAI_API_KEY").unsafeCast<String?>()
    val token = requireNotNull(apiKey) { "OPENAI_API_KEY environment variable must be set." }
    val openAI = OpenAI(token = token, logging = LoggingConfig(LogLevel.Info))
    val scope = CoroutineScope(coroutineContext)

    println("> Getting available models...")
    openAI.models().forEach(::println)

    println("\n> Getting model...")
    val ada = openAI.model(modelId = ModelId("gpt-3.5-turbo"))
    println(ada)

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

    println("\n> Create images...")
    val images = openAI.imageURL(
        creation = ImageCreation(
            prompt = "A cute baby sea otter",
            n = 2,
            size = ImageSize.is1024x1024
        )
    )
    println(images)

    println("\n> Edit images...")
    val imageEdit = ImageEdit(
        image = FileSource(path = Path("kotlin","image.png")),
        mask = FileSource(path = Path("kotlin","mask.png")),
        prompt = "a sunlit indoor lounge area with a pool containing a flamingo",
        n = 1,
        size = ImageSize.is1024x1024,
    )
    val imageEdits = openAI.imageURL(imageEdit)
    println(imageEdits)

    println("\n> Create chat completions...")
    val chatCompletionRequest = ChatCompletionRequest(
        model = ModelId("gpt-3.5-turbo"),
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

    println("\n>️ Create translation...")
    val translationRequest = TranslationRequest(
        audio = FileSource(path = Path("kotlin", "multilingual.wav")),
        model = ModelId("whisper-1"),
    )
    val translation = openAI.translation(translationRequest)
    println(translation)
}
