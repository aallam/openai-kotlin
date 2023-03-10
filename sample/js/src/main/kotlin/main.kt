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
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.api.moderation.ModerationRequest
import com.aallam.openai.client.OpenAI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import okio.FileSystem
import okio.NodeJsFileSystem
import okio.Path.Companion.toPath
import kotlin.coroutines.coroutineContext

@OptIn(BetaOpenAI::class)
suspend fun main() {
    val apiKey = js("process.env.OPENAI_API_KEY").unsafeCast<String?>()
    val token = requireNotNull(apiKey) { "OPENAI_API_KEY environment variable must be set." }
    val openAI = OpenAI(token = token)
    val scope = CoroutineScope(coroutineContext)

    println("> Getting available models...")
    openAI.models().forEach(::println)

    println("\n> Getting ada model...")
    val ada = openAI.model(modelId = ModelId("text-ada-001"))
    println(ada)

    println("\n>️ Creating completion...")
    val completionRequest = CompletionRequest(
        model = ada.id,
        prompt = "Somebody once told me the world is gonna roll me"
    )
    openAI.completion(completionRequest).choices.forEach(::println)

    println("\n>️ Creating completion stream...")
    openAI.completions(completionRequest)
        .onEach { print(it.choices[0].text) }
        .onCompletion { println() }
        .launchIn(scope)
        .join()

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
        image = FileSource(path = "kotlin/image.png".toPath(), fileSystem = NodeJsFileSystem),
        mask = FileSource(path = "kotlin/mask.png".toPath(), fileSystem = NodeJsFileSystem),
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
        audio = FileSource(path = "kotlin/micro-machines.wav".toPath(), fileSystem = NodeJsFileSystem),
        model = ModelId("whisper-1"),
    )
    val transcription = openAI.transcription(transcriptionRequest)
    println(transcription)

    println("\n>️ Create translation...")
    val translationRequest = TranslationRequest(
        audio = FileSource(path = "kotlin/multilingual.wav".toPath(), fileSystem = NodeJsFileSystem),
        model = ModelId("whisper-1"),
    )
    val translation = openAI.translation(translationRequest)
    println(translation)
}
