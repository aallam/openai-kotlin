# Getting Started

Create an instance of `OpenAI` client:

```kotlin
val openAI = OpenAI(apiKey)
```

> ℹ️ OpenAI encourages using environment variables for the API key. [Read more](https://help.openai.com/en/articles/5112595-best-practices-for-api-key-safety).

Use your `OpenAI` instance to make API requests.

- [Models](#models)
  - [List models](#list-models)
  - [Retrieve a model](#retrieve-a-model)
- [Completions](#completions)
  - [Create completion](#create-completion)
- [Chat](#chat)
  - [Create chat completion](#create-chat-completion)
- [Edits](#edits)
  - [Create edits](#create-edits)
- [Images](#images)
  - [Create image](#create-image-beta)
  - [Edit images](#edit-images-beta)
  - [Create image variation](#create-image-variation-beta)
- [Embeddings](#embeddings)
  - [Create embeddings](#create-embeddings)
- [Audio](#audio)
  - [Create transcription](#create-transcription-beta)
  - [Create translation](#create-translation-beta)
- [Files](#files)
  - [List files](#list-files)
  - [Upload file](#upload-file)
  - [Delete file](#delete-file)
  - [Retrieve file](#retrieve-file)
  - [Retrieve file content](#retrieve-file-content)
- [Fine-tunes](#fine-tunes)
  - [Create fine-tune](#create-fine-tune)
  - [List fine-tunes](#list-fine-tunes)
  - [Retrieve fine-tune](#retrieve-fine-tune)
  - [Cancel fine-tune](#cancel-fine-tune)
  - [List fine-tune events](#list-fine-tune-events)
  - [Delete fine-tune model](#delete-fine-tune-model)
- [Moderations](#moderations)
  - [Create moderation](#create-moderation)

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

## Completions

Given a prompt, the model will return one or more predicted completions, and can also return the probabilities of alternative tokens at each position.

### Create Completion

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

## Chat

Given a chat conversation, the model will return a chat completion response.

### Create chat completion `beta`

Creates a completion for the chat message.

```kotlin
val chatCompletionRequest = ChatCompletionRequest(
  model = ModelId("gpt-3.5-turbo"),
  messages = listOf(
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

## Edits 

Given a prompt and an instruction, the model will return an edited version of the prompt.

### Create edits

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

## Images

Given a prompt and/or an input image, the model will generate a new image.

### Create image `beta`

Creates an image given a prompt.

````kotlin
val images = openAI.imageURL( // or openAI.imageJSON
    creation = ImageCreation(
        prompt = "A cute baby sea otter",
        n = 2,
        size = ImageSize.is1024x1024
    )
)
````

### Edit images `beta`

Creates an edited or extended image given an original image and a prompt.

````kotlin
val images = openAI.imageURL( // or openAI.imageJSON
    edit = ImageEdit(
        image = FileSource(name = "<filename>", source = imageSource),
        mask = FileSource(name = "<filename>", source = maskSource),
        prompt = "a sunlit indoor lounge area with a pool containing a flamingo",
        n = 1,
        size = ImageSize.is1024x1024
    )
)
````

### Create image variation `beta`

Creates a variation of a given image.

````kotlin
val images = openAI.imageURL( // or openAI.imageJSON
    variation = ImageVariation(
        image = FileSource(name = "<filename>", source = imageSource),
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

## Audio

Learn how to turn audio into text.

### Create transcription `Beta`

Transcribes audio into the input language.

```kotlin
val request = TranscriptionRequest(
  audio = FileSource(name = "<filename>", source = audioSource),
  model = ModelId("whisper-1"),
)
val transcription = openAI.transcription(request)
```

### Create translation `Beta`

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

## Moderations

Given a input text, outputs if the model classifies it as violating OpenAI's content policy.

### Create moderation

Given a input text, outputs if the model classifies it as violating OpenAI's content policy.

````kotlin
val moderation = openAI.moderations(
    request = ModerationRequest(
        input = "I want to kill them."
    )
)
````
