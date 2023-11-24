# Getting Started

Create an instance of `OpenAI` client:

```kotlin
val openAI = OpenAI(apiKey)
```

> ℹ️ OpenAI encourages using environment variables for the API
> key. [Read more](https://help.openai.com/en/articles/5112595-best-practices-for-api-key-safety).

Use your `OpenAI` instance to make API requests.

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
  - [Create assistant file](#create-assistant-file)
  - [Retrieve assistant file](#retrieve-assistant-file)
  - [Delete assistant file](#delete-assistant-file)
  - [List assistant files](#list-assistant-files)
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
  - [Retrieve message file](#retrieve-message-file)
  - [List message files](#list-message-files)
- [Runs](#runs)
  - [Create run](#create-run)
  - [Retrieve run](#retrieve-run)
  - [Modify run](#modify-run)
  - [List runs](#list-runs)
  - [Cancel run](#cancel-run)
  - [Create thread and run](#create-thread-and-run)
  - [Retrieve a run step](#retrieve-a-run-step)
  - [List run steps](#list-run-steps)

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
val id = ModelId("text-ada-001")
val model: Model = openAI.model(id)
```

## Chat

Given a chat conversation, the model will return a chat completion response.

### Create chat completion

Creates a completion for the chat message.

```kotlin
val chatCompletionRequest = ChatCompletionRequest(
    model = ModelId("gpt-3.5-turbo"),
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
val completion: ChatCompletion = openAI.chatCompletion(chatCompletionRequest)
// or, as flow
val completions: Flow<ChatCompletionChunk> = openAI.chatCompletions(chatCompletionRequest)
```

## Images

Given a prompt and/or an input image, the model will generate a new image.

### Create image

Creates an image given a prompt.

````kotlin
val images = openAI.imageURL( // or openAI.imageJSON
    creation = ImageCreation(
        prompt = "A cute baby sea otter",
        model = ModelId("dall-e-3"),
        n = 2,
        size = ImageSize.is1024x1024
    )
)
````

### Edit images

Creates an edited or extended image given an original image and a prompt.

````kotlin
val images = openAI.imageURL( // or openAI.imageJSON
    edit = ImageEdit(
        image = FileSource(name = "<filename>", source = imageSource),
        model = ModelId("dall-e-2"),
        mask = FileSource(name = "<filename>", source = maskSource),
        prompt = "a sunlit indoor lounge area with a pool containing a flamingo",
        n = 1,
        size = ImageSize.is1024x1024
    )
)
````

### Create image variation

Creates a variation of a given image.

````kotlin
val images = openAI.imageURL( // or openAI.imageJSON
    variation = ImageVariation(
        image = FileSource(name = "<filename>", source = imageSource),
        model = ModelId("dall-e-3"),
        n = 1,
        size = ImageSize.is1024x1024
    )
)
````

## Embeddings

Get a vector representation of a given input that can be easily consumed by machine learning models and algorithms.

### Create embeddings

Creates an embedding vector representing the input text.

````kotlin
val embeddings = openAI.embeddings(
    request = EmbeddingRequest(
        model = ModelId("text-similarity-babbage-001"),
        input = listOf("The food was delicious and the waiter...")
    )
)
````

## Fine-tuning

Manage fine-tuning jobs to tailor a model to your specific training data.

### Create fine-tuning job

Creates a job that fine-tunes a specified model from a given dataset.

Response includes details of the enqueued job including job status and the name of the fine-tuned models once complete.

#### No Hyperparameters

```kotlin
val request = FineTuningRequest(
    trainingFile = FileId("file-abc123"),
    model = ModelId("gpt-3.5-turbo"),
)
val fineTuningJob = client.fineTuningJob(request)
```

#### Hyperparameters

```kotlin
val request = FineTuningRequest(
    trainingFile = FileId("file-abc123"),
    model = ModelId("gpt-3.5-turbo"),
    hyperparameters = Hyperparameters(nEpochs = 2),
)
val fineTuningJob = client.fineTuningJob(request)
```

#### Validation File

```kotlin
val request = FineTuningRequest(
    trainingFile = FileId("file-abc123"),
    validation_file = FileId("file-def345"),
    model = ModelId("gpt-3.5-turbo"),
)
val fineTuningJob = client.fineTuningJob(request)
```

### List fine-tuning jobs

List your organization's fine-tuning jobs

```kotlin
val fineTuningJobs = client.fineTuningJobs(limit = 2)
```

### Retrieve fine-tuning job

Get info about a fine-tuning job.

```kotlin
val id = FineTuningId("ft-AF1WoRqd3aJAHsqc9NY7iL8F")
val fineTuningJob = client.fineTuningJob(id)
```

### Cancel fine-tuning

Immediately cancel a fine-tune job.

```kotlin
val id = FineTuningId("ftjob-abc12")
client.cancel(id)
```

### List fine-tuning events

Get status updates for a fine-tuning job.

```kotlin
val id = FineTuningId("ftjob-abc12")
val fineTuningEvents = client.fineTuningEvents(id)
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
    audio = FileSource(name = "<filename>", source = audioSource),
    model = ModelId("whisper-1"),
)
val transcription = openAI.transcription(request)
```

### Create translation

Translates audio into English.

```kotlin
val request = TranslationRequest(
    audio = FileSource(name = "<filename>", source = audioSource),
    model = ModelId("whisper-1"),
)
val translation = openAI.translation(request)
```

## Files

Files are used to upload documents that can be used with features like [Fine-tuning](#fine-tunes).

### List files

Returns a list of files that belong to the user's organization.

````kotlin
val files = openAI.files()
````

### Upload file

Upload a file that contains document(s) to be used across various endpoints/features.
Currently, the size of all the files uploaded by one organization can be up to 1 GB.

````kotlin
val file = openAI.file(
    request = FileUpload(
        file = source,
        purpose = Purpose("fine-tune")
    )
)
````

### Delete file

Delete a file.

````kotlin
openAI.delete(fileId)
````

### Retrieve file

Returns information about a specific file.

````kotlin
val file = openAI.file(fileId)
````

### Retrieve file content

Returns the contents of the specified file

````kotlin
val bytes = openAI.download(fileId)
````

## Moderations

Given an input text, outputs if the model classifies it as violating OpenAI's content policy.

### Create moderation

Classifies if text violates OpenAI's Content Policy

````kotlin
val moderation = openAI.moderations(
    request = ModerationRequest(
        input = "I want to kill them."
    )
)
````

## Hosts

This library has support for custom ChatGPT URLs. The host used by default is `https://api.openai.com/v1/`, as you would
expect, and support for Azure hosted ChatGPT is built in as well.

### Azure

To connect to an Azure hosted instance, use the `OpenAIHost.azure` function like so:

````kotlin
val host = OpenAIHost.azure(
    resourceName = "The name of your Azure OpenAI Resource.",
    deploymentId = "The name of your model deployment.",
    apiVersion = "The API version to use for this operation. This parameter should follow the YYYY-MM-DD format.",
)
val config = OpenAIConfig(
    host = host,
    token = "Your API token",
)
val openAI = OpenAI(config)
````

### Other hosts

You can connect to whatever host you like by constructing your own `OpenAIHost` instance. Otherwise, it's exactly the
same as the above Azure example.

````kotlin
val host = OpenAIHost(
    baseUrl = "http://localhost:8080",
)
val config = OpenAIConfig(
    host = host,
    token = "Your API token",
)
val openAI = OpenAI(config)
````

---

## Completions

Given a prompt, the model will return one or more predicted completions, and can also return the probabilities of
alternative tokens at each position.

### Create Completion `legacy`

Creates a completion for the provided prompt and parameters

```kotlin
val completionRequest = CompletionRequest(
    model = ModelId("text-ada-001"),
    prompt = "Somebody once told me the world is gonna roll me",
    echo = true
)
val completion: TextCompletion = openAI.completion(completionRequest)
// or, as flow
val completions: Flow<TextCompletion> = openAI.completions(completionRequest)
```

---

## Fine-tunes

Manage fine-tuning jobs to tailor a model to your specific training data.

### Create fine-tune

Creates a job that fine-tunes a specified model from a given dataset.

Response includes details of the enqueued job including job status and the name of the fine-tuned models once complete.

````kotlin
val fineTune = openAI.fineTune(
    request = FineTuneRequest(
        trainingFile = trainingFile,
        model = ModelId("ada")
    )
)
````

### List fine-tunes

List your organization's fine-tuning jobs

```kotlin
val fineTunes = openAI.fineTunes()
```

### Retrieve fine-tune

Gets info about the fine-tune job.

```kotlin
val finetune = openAI.fineTune(fineTune.id)
```

### Cancel fine-tune

Immediately cancel a fine-tune job.

```kotlin
val finetune = openAI.cancel(fineTune.id)
```

### List fine-tune events

Get fine-grained status updates for a fine-tune job.

```kotlin
val fineTuneEvents: List<FineTuneEvent> = openAI.fineTuneEvents(fineTune.id)
// or, as flow
val fineTuneEvents: Flow<FineTuneEvent> = openAI.fineTuneEventsFlow(fineTune.id)
````

### Delete fine-tune model

Delete a fine-tuned model. You must have the Owner role in your organization.

```kotlin
openAI.delete(fileId)
```

## Edits

Given a prompt and an instruction, the model will return an edited version of the prompt.

### Create edits `Deprecated`

Creates a new edit for the provided input, instruction, and parameters.

```kotlin
val edit = openAI.edit(
    request = EditsRequest(
        model = ModelId("text-davinci-edit-001"),
        input = "What day of the wek is it?",
        instruction = "Fix the spelling mistakes"
    )
)
```

## Assistants

Build assistants that can call models and use tools to perform tasks.

### Create assistant

Create an assistant with a model and instructions.

```kotlin
val assistant = openAI.assistant(
  request = AssistantRequest(
    name = "Math Tutor",
    tools = listOf(AssistantTool.CodeInterpreter),
    model = ModelId("gpt-4")
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
    id = AssistantId("asst_abc123"), request = AssistantRequest(
        instructions = "You are an HR bot, and you have access to files to answer employee questions about company policies. Always response with info from either of the files.",
        tools = listOf(AssistantTool.RetrievalTool),
        model = ModelId("gpt-4"),
        fileIds = listOf(FileId("file-abc123"), FileId("file-abc123")),
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

### Create assistant file

Create an assistant file by attaching a File to an assistant.

```kotlin
val assistant = openAI.createFile(
  assistantId = AssistantId("asst_abc123"),
  fileId = FileId("file_abc123")
)
```

### Retrieve assistant file

Retrieve an assistant file.

```kotlin
val assistant = openAI.file(
  assistantId = AssistantId("asst_abc123"),
  fileId = FileId("file_abc123")
)
```

### Delete assistant file

Delete an assistant file.

```kotlin
openAI.delete(
  assistantId = AssistantId("asst_abc123"),
  fileId = FileId("file_abc123")
)
```

### List assistant files

Returns a list of assistant files.

```kotlin
val assistantFiles = openAI.files(assistantId = AssistantId("asst_abc123"))
```

## Threads

Create threads that assistants can interact with.

### Create thread

Create a thread with a prompt and instructions.

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

Create messages within threads

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
  messageId = MessageId("message_abc123")
)
```

### Modify message

Modifies a message.

```kotlin
val message = openAI.message(
  threadId = ThreadId("thread_abc123"),
  messageId = MessageId("message_abc123"),
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

### Retrieve message file

A list of files attached to a `Message`.

```kotlin
val messageFile = openAI.messageFile(
  threadId = ThreadId("thread_abc123"),
  messageId = MessageId("message_abc123"),
  fileId = FileId("file_abc123")
)
```

### List message files

Returns a list of message files.

```kotlin
val messageFiles = openAI.messageFiles(
  threadId = ThreadId("thread_abc123"),
  messageId = MessageId("message_abc123")
)
```

## Runs

Represents an execution run on a thread.

### Create run

Create a run.

```kotlin
val run = openAI.createRun(
  threadId = ThreadId("thread_abc123"),
  request = RunRequest(assistantId = AssistantId("assistant_abc123")),
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

Cancel a run that is `Status.InProgress`

```kotlin
openAI.cancel(
  threadId = ThreadId("thread_abc123"),
  runId = RunId("run_abc123")
)
```

### Create thread and run

Create a thread and run it in one request.

```kotlin
openAI.createThreadRun(
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
