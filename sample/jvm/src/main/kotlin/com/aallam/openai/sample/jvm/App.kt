package com.aallam.openai.sample.jvm

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.audio.TranscriptionRequest
import com.aallam.openai.api.audio.TranslationRequest
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.chat.FunctionCall
import com.aallam.openai.api.chat.ChatCompletionFunction
import com.aallam.openai.api.chat.JsonData
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
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking
import okio.FileSystem
import okio.Path.Companion.toPath
import kotlinx.serialization.json.put

@OptIn(BetaOpenAI::class)
fun main() = runBlocking {
    val apiKey = System.getenv("OPENAI_API_KEY")
    val token = requireNotNull(apiKey) { "OPENAI_API_KEY environment variable must be set." }
    val openAI = OpenAI(token = token, logging = LoggingConfig(LogLevel.All))

    println("> Getting available engines...")
    openAI.models().forEach(::println)

    println("\n> Getting ada engine...")

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
        .launchIn(this)
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
        image = FileSource(path = "image.png".toPath(), fileSystem = FileSystem.RESOURCES),
        mask = FileSource(path = "image.png".toPath(), fileSystem = FileSystem.RESOURCES),
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

    println("> Create Chat Completion function call...")
    val chatCompletionCreateFunctionCall = ChatCompletionRequest(
        model = ModelId("gpt-3.5-turbo-0613"),
        messages = listOf(
            ChatMessage(
                role = ChatRole.System,
                content = "You are a helpful assistant that translates English to French."
            ),
            ChatMessage(
                role = ChatRole.User,
                content = "Translate the following English text to French: “OpenAI is awesome!”"
            )
        ),
        functionCall = FunctionCall.forceCall("translate"),
        functions = listOf(
            ChatCompletionFunction(
                name = "translate",
                description = "Translate English to French",
                parameters = JsonData.fromString(
                    "{\"type\": \"object\", \"properties\": {\"text\": {\"type\": \"string\"}}}"
                ),
            )
        )
    )
    openAI.chatCompletion(chatCompletionCreateFunctionCall).choices.forEach(::println)
    println("> Process Chat Completion function call...")
    val chatFunctionReturn = ChatCompletionRequest(
        model = ModelId("gpt-3.5-turbo-0613"),
        messages = listOf(
            ChatMessage(
                role = ChatRole.System,
                content = "You are a helpful assistant that uses a function to translates English to French.\n" +
                        "Use only the result of the function call as the response."
            ),
            ChatMessage(
                role = ChatRole.User,
                content = "Translate the following English text to French: “OpenAI is awesome!”"
            ),
            ChatMessage(
                role = ChatRole.Assistant,
                content = "None",
                functionCall = JsonData.builder {
                    put("name", "translate")
                    put("arguments", """{"text": "OpenAI is awesome!"}""")
                }

            ),
            ChatMessage(
                role = ChatRole.Function,
                content = "openai est super !",
                name = "translate",
            )
        ),
    )
    openAI.chatCompletion(chatFunctionReturn).choices.forEach(::println)

    println("\n>️ Creating chat completions stream...")
    openAI.chatCompletions(chatCompletionRequest)
        .onEach { print(it.choices.first().delta?.content.orEmpty()) }
        .onCompletion { println() }
        .launchIn(this)
        .join()

    println("\n>️ Create transcription...")
    val transcriptionRequest = TranscriptionRequest(
        audio = FileSource(path = "micro-machines.wav".toPath(), fileSystem = FileSystem.RESOURCES),
        model = ModelId("whisper-1"),
    )
    val transcription = openAI.transcription(transcriptionRequest)
    println(transcription)

    println("\n>️ Create translation...")
    val translationRequest = TranslationRequest(
        audio = FileSource(path = "multilingual.wav".toPath(), fileSystem = FileSystem.RESOURCES),
        model = ModelId("whisper-1"),
    )
    val translation = openAI.translation(translationRequest)
    println(translation)
}
