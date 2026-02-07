# Getting Started

Create an instance of `OpenAI` client:

```kotlin
val openAI = OpenAI(apiKey)
```

> ℹ️ OpenAI encourages using environment variables for the API
> key. [Read more](https://help.openai.com/en/articles/5112595-best-practices-for-api-key-safety).

Use your `OpenAI` instance to make API requests.

- [Responses](#responses)
  - [Create response](#create-response)
  - [Retrieve a response](#retrieve-a-response)
  - [Cancel a response](#cancel-a-response)
  - [Delete a response](#delete-a-response)
  - [List response input items](#list-response-input-items)
- [Models](#models)
  - [List models](#list-models)
  - [Retrieve a model](#retrieve-a-model)
- [Chat](#chat)
  - [Create chat completion](#create-chat-completion)
- [Images](#images)
  - [Create image](#create-image)
  - [Edit images](#edit-images)
  - [Create image variation](#create-image-variation)
- [Embeddings](#embeddings)
  - [Create embeddings](#create-embeddings)
- [Fine-tuning](#fine-tuning)
  - [Create fine-tuning job](#create-fine-tuning-job)
  - [List fine-tuning jobs](#list-fine-tuning-jobs)
  - [Retrieve fine-tuning job](#retrieve-fine-tuning-job)
  - [Cancel fine-tuning](#cancel-fine-tuning)
  - [List fine-tuning events](#list-fine-tuning-events)
- [Audio](#audio)
  - [Create speech](#create-speech)
  - [Create transcription](#create-transcription)
  - [Create translation](#create-translation)
- [Files](#files)
  - [List files](#list-files)
  - [Upload file](#upload-file)
  - [Delete file](#delete-file)
  - [Retrieve file](#retrieve-file)
  - [Retrieve file content](#retrieve-file-content)
- [Moderations](#moderations)
  - [Create moderation](#create-moderation)
- [Batch](#batch)
  - [Create batch](#create-batch)
  - [Retrieve batch](#retrieve-batch)
  - [List batches](#list-batches)
  - [Cancel batch](#cancel-batch)
- [Vector stores](#vector-stores)
  - [Create vector store](#create-vector-store)
  - [List vector stores](#list-vector-stores)
  - [Retrieve vector store](#retrieve-vector-store)
  - [Update vector store](#update-vector-store)
  - [Delete vector store](#delete-vector-store)
  - [Attach file to vector store](#attach-file-to-vector-store)
  - [Batch files into vector store](#batch-files-into-vector-store)
- [Hosts](#hosts)
  - [Azure](#azure)
  - [Other hosts](#other-hosts)

#### Beta

- [Assistants](#assistants)
  - [Create assistant](#create-assistant)
  - [Retrieve assistant](#retrieve-assistant)
  - [Modify assistant](#modify-assistant)
  - [Delete assistant](#delete-assistant)
  - [List assistants](#list-assistants)
- [Threads](#threads)
  - [Create thread](#create-thread)
  - [Retrieve thread](#retrieve-thread)
  - [Modify thread](#modify-thread)
  - [Delete thread](#delete-thread)
- [Messages](#messages)
  - [Create message](#create-message)
  - [Retrieve message](#retrieve-message)
  - [Modify message](#modify-message)
  - [List messages](#list-messages)
- [Runs](#runs)
  - [Create run](#create-run)
  - [Retrieve run](#retrieve-run)
  - [Modify run](#modify-run)
  - [List runs](#list-runs)
  - [Cancel run](#cancel-run)
  - [Create thread and run](#create-thread-and-run)
  - [Retrieve a run step](#retrieve-a-run-step)
  - [List run steps](#list-run-steps)
  - [Event streaming](#event-streaming)

#### Deprecated

- [Completions](#completions)
  - [Create completion](#create-completion-legacy)
- [Fine-tunes](#fine-tunes)
  - [Create fine-tune](#create-fine-tune)
  - [List fine-tunes](#list-fine-tunes)
  - [Retrieve fine-tune](#retrieve-fine-tune)
  - [Cancel fine-tune](#cancel-fine-tune)
  - [List fine-tune events](#list-fine-tune-events)
  - [Delete fine-tune model](#delete-fine-tune-model)
- [Edits](#edits)
  - [Create edits](#create-edits-deprecated)

## Responses

Create model responses with the Responses API.

### Create response

```kotlin
val response = openAI.response(
    request = ResponseRequest(
        model = ModelId("gpt-4.1"),
        input = ResponseInput("Write a haiku about Kotlin.")
    )
)

println(response.outputText)
```

### Retrieve a response

```kotlin
val responseId = ResponseId("resp_123")
val response = openAI.response(responseId)
```

### Cancel a response

```kotlin
val responseId = ResponseId("resp_123")
val cancelled = openAI.cancel(responseId)
```

### Delete a response

```kotlin
val responseId = ResponseId("resp_123")
val deleted = openAI.delete(responseId)
```

### List response input items

```kotlin
val responseId = ResponseId("resp_123")
val inputItems = openAI.responseInputItems(id = responseId, limit = 20)
```

## Models

List and describe the various [models](https://platform.openai.com/docs/models) available in the API.
You can refer to the Models documentation to understand what models are available and the differences between them.

### List models

Lists the currently available models, and provides basic information about each one such as the owner and availability.

```kotlin
val models: List<Model> = openAI.models()
```

### Retrieve a model

Retrieves a model instance, providing basic information about the model such as the owner and permissioning.

```kotlin
val id = ModelId("gpt-4.1")
val model: Model = openAI.model(id)
```

## Chat

Given a chat conversation, the model will return a chat completion response.

### Create chat completion

Creates a completion for the chat message.

```kotlin
val request = ChatCompletionRequest(
    model = ModelId("gpt-4o-mini"),
    messages = listOf(
        ChatMessage(
            role = ChatRole.System,
            content = "You are a helpful assistant!"
        ),
        ChatMessage(
            role = ChatRole.User,
            content = "Hello!"
        )
    )
)

val completion: ChatCompletion = openAI.chatCompletion(request)

// Or stream as a Flow
val chunks: Flow<ChatCompletionChunk> = openAI.chatCompletions(request)
```

## Images

Given a prompt and/or an input image, the model will generate a new image.

### Create image

Creates an image given a prompt.

```kotlin
val images = openAI.imageURL( // or openAI.imageJSON
    creation = ImageCreation(
        prompt = "A cute baby sea otter",
        model = ModelId("dall-e-3"),
        n = 1,
        size = ImageSize.is1024x1024
    )
)
```

### Edit images

Creates an edited or extended image given an original image and a prompt.

```kotlin
val images = openAI.imageURL( // or openAI.imageJSON
    edit = ImageEdit(
        image = FileSource(path = Path("image.png")),
        model = ModelId("dall-e-2"),
        mask = FileSource(path = Path("mask.png")),
        prompt = "a sunlit indoor lounge area with a pool containing a flamingo",
        n = 1,
        size = ImageSize.is1024x1024
    )
)
```

### Create image variation

Creates a variation of a given image.

```kotlin
val images = openAI.imageURL( // or openAI.imageJSON
    variation = ImageVariation(
        image = FileSource(path = Path("image.png")),
        model = ModelId("dall-e-2"),
        n = 1,
        size = ImageSize.is1024x1024
    )
)
```

## Embeddings

Get a vector representation of a given input that can be easily consumed by machine learning models and algorithms.

### Create embeddings

Creates an embedding vector representing the input text.

```kotlin
val embeddings = openAI.embeddings(
    request = EmbeddingRequest(
        model = ModelId("text-embedding-3-small"),
        input = listOf("The food was delicious and the waiter was very friendly.")
    )
)
```

## Fine-tuning

Manage fine-tuning jobs to tailor a model to your specific training data.

### Create fine-tuning job

Creates a job that fine-tunes a specified model from a given dataset.

#### No Hyperparameters

```kotlin
val request = FineTuningRequest(
    trainingFile = FileId("file-abc123"),
    model = ModelId("gpt-3.5-turbo"),
)
val fineTuningJob = openAI.fineTuningJob(request)
```

#### Hyperparameters

```kotlin
val request = FineTuningRequest(
    trainingFile = FileId("file-abc123"),
    model = ModelId("gpt-3.5-turbo"),
    hyperparameters = Hyperparameters(nEpochs = 2),
)
val fineTuningJob = openAI.fineTuningJob(request)
```

#### Validation File

```kotlin
val request = FineTuningRequest(
    trainingFile = FileId("file-abc123"),
    validationFile = FileId("file-def345"),
    model = ModelId("gpt-3.5-turbo"),
)
val fineTuningJob = openAI.fineTuningJob(request)
```

### List fine-tuning jobs

List your organization's fine-tuning jobs.

```kotlin
val fineTuningJobs = openAI.fineTuningJobs(limit = 20)
```

### Retrieve fine-tuning job

Get info about a fine-tuning job.

```kotlin
val id = FineTuningId("ftjob-abc123")
val fineTuningJob = openAI.fineTuningJob(id)
```

### Cancel fine-tuning

Immediately cancel a fine-tuning job.

```kotlin
val id = FineTuningId("ftjob-abc123")
openAI.cancel(id)
```

### List fine-tuning events

Get status updates for a fine-tuning job.

```kotlin
val id = FineTuningId("ftjob-abc123")
val fineTuningEvents = openAI.fineTuningEvents(id)
```

## Audio

Learn how to turn audio into text.

### Create speech

Generates audio from the input text.

```kotlin
val rawAudio = openAI.speech(
    request = SpeechRequest(
        model = ModelId("tts-1"),
        input = "The quick brown fox jumped over the lazy dog.",
        voice = Voice.Alloy,
    )
)
```

### Create transcription

Transcribes audio into the input language.

```kotlin
val request = TranscriptionRequest(
    audio = FileSource(path = Path("micro-machines.wav")),
    model = ModelId("whisper-1"),
)
val transcription = openAI.transcription(request)
```

### Create translation

Translates audio into English.

```kotlin
val request = TranslationRequest(
    audio = FileSource(path = Path("multilingual.wav")),
    model = ModelId("whisper-1"),
)
val translation = openAI.translation(request)
```

## Files

Files are used to upload documents that can be used across features.

### List files

Returns a list of files that belong to the user's organization.

```kotlin
val files = openAI.files()
```

### Upload file

Upload a file that contains document(s) to be used across various endpoints/features.

```kotlin
val file = openAI.file(
    request = FileUpload(
        file = FileSource(path = Path("training.jsonl")),
        purpose = Purpose("fine-tune")
    )
)
```

### Delete file

Delete a file.

```kotlin
openAI.delete(file.id)
```

### Retrieve file

Returns information about a specific file.

```kotlin
val file = openAI.file(FileId("file-abc123"))
```

### Retrieve file content

Returns the contents of the specified file.

```kotlin
val bytes = openAI.download(FileId("file-abc123"))
```

## Moderations

Given an input text, outputs if the model classifies it as violating OpenAI's content policy.

### Create moderation

Classifies if text violates OpenAI's content policy.

```kotlin
val moderation = openAI.moderations(
    request = ModerationRequest(
        model = ModerationModel.Latest,
        input = "I want to kill them."
    )
)
```

## Batch

Create and manage asynchronous batches.

### Create batch

```kotlin
val request = BatchRequest(
    inputFileId = FileId("file-abc123"),
    endpoint = Endpoint.Completions,
    completionWindow = CompletionWindow.TwentyFourHours
)

val batch = openAI.batch(request)
```

### Retrieve batch

```kotlin
val batch = openAI.batch(BatchId("batch_abc123"))
```

### List batches

```kotlin
val batches = openAI.batches(limit = 20)
```

### Cancel batch

```kotlin
val cancelled = openAI.cancel(BatchId("batch_abc123"))
```

## Vector Stores

Store and index files for `file_search` use cases.

### Create vector store

```kotlin
val vectorStore = openAI.createVectorStore(
    request = VectorStoreRequest(name = "Support FAQ")
)
```

### List vector stores

```kotlin
val vectorStores = openAI.vectorStores(limit = 20)
```

### Retrieve vector store

```kotlin
val vectorStore = openAI.vectorStore(VectorStoreId("vs_abc123"))
```

### Update vector store

```kotlin
val updated = openAI.updateVectorStore(
    id = VectorStoreId("vs_abc123"),
    request = VectorStoreRequest(name = "Support FAQ v2")
)
```

### Delete vector store

```kotlin
val deleted = openAI.delete(VectorStoreId("vs_abc123"))
```

### Attach file to vector store

```kotlin
val vectorStoreFile = openAI.createVectorStoreFile(
    id = VectorStoreId("vs_abc123"),
    request = VectorStoreFileRequest(fileId = FileId("file-abc123"))
)

val files = openAI.vectorStoreFiles(id = VectorStoreId("vs_abc123"))
val removed = openAI.delete(id = VectorStoreId("vs_abc123"), fileId = FileId("file-abc123"))
```

### Batch files into vector store

```kotlin
val batch = openAI.createVectorStoreFilesBatch(
    id = VectorStoreId("vs_abc123"),
    request = FileBatchRequest(fileIds = listOf(FileId("file-abc123"), FileId("file-def456")))
)

val retrieved = openAI.vectorStoreFileBatch(
    vectorStoreId = VectorStoreId("vs_abc123"),
    batchId = batch.id
)

val batchFiles = openAI.vectorStoreFilesBatches(
    vectorStoreId = VectorStoreId("vs_abc123"),
    batchId = batch.id
)

val cancelled = openAI.cancel(
    vectorStoreId = VectorStoreId("vs_abc123"),
    batchId = batch.id
)
```

## Hosts

This library supports custom OpenAI-compatible hosts. The default host is `https://api.openai.com/v1/`.

### Azure

To connect to an Azure-hosted instance, use `OpenAIHost.azure`:

```kotlin
val host = OpenAIHost.azure(
    resourceName = "your-resource-name",
    deploymentId = "your-deployment-id",
    apiVersion = "2024-10-21",
)

val config = OpenAIConfig(
    host = host,
    token = "your-api-token",
)

val openAI = OpenAI(config)
```

### Other hosts

You can connect to any compatible host by constructing your own `OpenAIHost` instance.

```kotlin
val host = OpenAIHost(
    baseUrl = "http://localhost:8080/v1/",
)

val config = OpenAIConfig(
    host = host,
    token = "your-api-token",
)

val openAI = OpenAI(config)
```

---

## Assistants

Build assistants that can call models and use tools to perform tasks.

### Create assistant

Create an assistant with a model and instructions.

```kotlin
val assistant = openAI.assistant(
    request = AssistantRequest(
        name = "Math Tutor",
        tools = listOf(AssistantTool.CodeInterpreter),
        model = ModelId("gpt-4o-mini")
    )
)
```

### Retrieve assistant

Retrieves an assistant.

```kotlin
val assistant = openAI.assistant(id = AssistantId("asst_abc123"))
```

### Modify assistant

Modifies an assistant.

```kotlin
val assistant = openAI.assistant(
    id = AssistantId("asst_abc123"),
    request = AssistantRequest(
        instructions = "You are an HR bot. Use file search to answer policy questions.",
        tools = listOf(AssistantTool.FileSearch),
        toolResources = ToolResources(
            fileSearch = FileSearchResources(vectorStoreIds = listOf(VectorStoreId("vs_abc123")))
        ),
        model = ModelId("gpt-4o-mini"),
    )
)
```

### Delete assistant

Delete an assistant.

```kotlin
openAI.delete(id = AssistantId("asst_abc123"))
```

### List assistants

Returns a list of assistants.

```kotlin
val assistants = openAI.assistants()
```

## Threads

Create threads that assistants can interact with.

### Create thread

Create a thread with optional initial messages.

```kotlin
val thread = openAI.thread()
```

### Retrieve thread

Retrieves a thread.

```kotlin
val thread = openAI.thread(id = ThreadId("thread_abc123"))
```

### Modify thread

Modifies a thread.

```kotlin
val thread = openAI.thread(
    id = ThreadId("thread_abc123"),
    metadata = mapOf(
        "modified" to "true",
        "user" to "abc123"
    )
)
```

### Delete thread

Delete a thread.

```kotlin
openAI.delete(id = ThreadId("thread_abc123"))
```

## Messages

Create messages within threads.

### Create message

Create a message.

```kotlin
val message = openAI.message(
    threadId = ThreadId("thread_abc123"),
    request = MessageRequest(
        role = Role.User,
        content = "How does AI work? Explain it in simple terms.",
    )
)
```

### Retrieve message

Retrieve a message.

```kotlin
val message = openAI.message(
    threadId = ThreadId("thread_abc123"),
    messageId = MessageId("msg_abc123")
)
```

### Modify message

Modifies a message.

```kotlin
val message = openAI.message(
    threadId = ThreadId("thread_abc123"),
    messageId = MessageId("msg_abc123"),
    metadata = mapOf(
        "modified" to "true",
        "user" to "abc123"
    )
)
```

### List messages

Returns a list of messages for a given thread.

```kotlin
val messages = openAI.messages(threadId = ThreadId("thread_abc123"))
```

## Runs

Represents an execution run on a thread.

### Create run

Create a run.

```kotlin
val run = openAI.createRun(
    threadId = ThreadId("thread_abc123"),
    request = RunRequest(assistantId = AssistantId("asst_abc123")),
)
```

### Retrieve run

Retrieves a run.

```kotlin
val run = openAI.getRun(
    threadId = ThreadId("thread_abc123"),
    runId = RunId("run_abc123")
)
```

### Modify run

Modifies a run.

```kotlin
val run = openAI.updateRun(
    threadId = ThreadId("thread_abc123"),
    runId = RunId("run_abc123"),
    metadata = mapOf("user_id" to "user_abc123")
)
```

### List runs

Returns a list of runs belonging to a thread.

```kotlin
val runs = openAI.runs(threadId = ThreadId("thread_abc123"))
```

### Cancel run

Cancel a run that is `Status.InProgress`.

```kotlin
val cancelled = openAI.cancel(
    threadId = ThreadId("thread_abc123"),
    runId = RunId("run_abc123")
)
```

### Create thread and run

Create a thread and run it in one request.

```kotlin
val run = openAI.createThreadRun(
    request = ThreadRunRequest(
        assistantId = AssistantId("asst_abc123"),
        thread = ThreadRequest(
            messages = listOf(
                ThreadMessage(
                    role = Role.User,
                    content = "Explain deep learning to a 5 year old."
                )
            )
        ),
    )
)
```

### Retrieve a run step

Retrieves a run step.

```kotlin
val runStep = openAI.runStep(
    threadId = ThreadId("thread_abc123"),
    runId = RunId("run_abc123"),
    stepId = RunStepId("step_abc123")
)
```

### List run steps

Returns a list of run steps belonging to a run.

```kotlin
val runSteps = openAI.runSteps(
    threadId = ThreadId("thread_abc123"),
    runId = RunId("run_abc123")
)
```

### Event streaming

Create a thread+run and process streaming events.

```kotlin
openAI
    .createStreamingThreadRun(
        request = ThreadRunRequest(
            assistantId = AssistantId("asst_abc123"),
            thread = ThreadRequest(
                messages = listOf(
                    ThreadMessage(
                        role = Role.User,
                        content = "Explain deep learning to a 5 year old."
                    )
                )
            )
        )
    )
    .onEach { event -> println(event.type) }
    .collect()
```

Get typed data from an `AssistantStreamEvent`:

```kotlin
when (assistantStreamEvent.type) {
    AssistantStreamEventType.THREAD_CREATED -> {
        val thread = assistantStreamEvent.getData<Thread>()
    }
    AssistantStreamEventType.THREAD_MESSAGE_CREATED -> {
        val message = assistantStreamEvent.getData<Message>()
    }
    AssistantStreamEventType.UNKNOWN -> {
        val raw = assistantStreamEvent.data
    }
    else -> Unit
}
```

If a new event type is released before the library is updated, you can deserialize custom payloads with your own serializer:

```kotlin
if (assistantStreamEvent.type == AssistantStreamEventType.UNKNOWN) {
    val data = assistantStreamEvent.getData(myCustomSerializer)
}
```

---

## Completions

Given a prompt, the model will return one or more predicted completions, and can also return token probabilities.

### Create completion `legacy`

```kotlin
val request = CompletionRequest(
    model = ModelId("text-ada-001"),
    prompt = "Somebody once told me the world is gonna roll me",
    echo = true
)

val completion: TextCompletion = openAI.completion(request)

// Or stream as Flow
val completions: Flow<TextCompletion> = openAI.completions(request)
```

---

## Fine-tunes

Legacy fine-tunes API.

### Create fine-tune

```kotlin
val fineTune = openAI.fineTune(
    request = FineTuneRequest(
        trainingFile = FileId("file-abc123"),
        model = ModelId("ada")
    )
)
```

### List fine-tunes

```kotlin
val fineTunes = openAI.fineTunes()
```

### Retrieve fine-tune

```kotlin
val fineTune = openAI.fineTune(FineTuneId("ft-abc123"))
```

### Cancel fine-tune

```kotlin
val fineTune = openAI.cancel(FineTuneId("ft-abc123"))
```

### List fine-tune events

```kotlin
val events: List<FineTuneEvent> = openAI.fineTuneEvents(FineTuneId("ft-abc123"))

// Or stream as Flow
val eventsFlow: Flow<FineTuneEvent> = openAI.fineTuneEventsFlow(FineTuneId("ft-abc123"))
```

### Delete fine-tune model

```kotlin
val deleted = openAI.delete(ModelId("ft:gpt-3.5-turbo:org:custom:abc123"))
```

## Edits

Given a prompt and an instruction, the model returns an edited version of the prompt.

### Create edits `Deprecated`

```kotlin
val edit = openAI.edit(
    request = EditsRequest(
        model = ModelId("text-davinci-edit-001"),
        input = "What day of the wek is it?",
        instruction = "Fix the spelling mistakes"
    )
)
```
