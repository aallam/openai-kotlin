# 📁 `FileSource` 

`FileSource` is a class designed to represent a file resource in the OpenAI API. It allows you to pass a file to the API using two different methods:
1. By providing the file path and the corresponding file system.
2. By providing the file name and its Source.

In this documentation, we will explain both methods in detail.

## Method 1: Using the file path and the corresponding file system

To use this method, you need to provide the file path as a `Path` object and the corresponding `FileSystem`.

### Usage
```kotlin
import okio.FileSystem
import okio.Path

val fileSource = FileSource(path = "your-file-path".toPath(), fileSystem = FileSystem.YOUR_FILE_SYSTEM)
```

### Example
```kotlin
val request = TranscriptionRequest(
    audio = FileSource(path = "micro-machines.wav".toPath(), fileSystem = FileSystem.RESOURCES),
    model = ModelId("whisper-1"),
)
val transcription = openAI.transcription(request)
```

## Method 2: Using the file name and its source

To use this method, you need to provide the file name as a `String` and the corresponding `Source`.

### Usage
```kotlin
import okio.asSource

val fileSource = FileSource(name = "your-file-name", source = yourByteArray.asSource())
```

### Example
```kotlin
val speedTalkingUrl = "https://github.com/aallam/sample-data/raw/main/openai/audio/micro-machines.wav"
val audioBytes: ByteArray = httpClient.get(speedTalkingUrl).body()
val request = transcriptionRequest {
    audio = FileSource(name = "micro-machines.wav", source = audioBytes.asSource())
    model = ModelId("whisper-1")
}
val transcription = openAI.transcription(request)
```

In this example, the `speedTalkingUrl` variable contains the URL of the audio file. The `httpClient.get()` method downloads the audio file, and the `body()` function returns the audio file as a byte array (`ByteArray`). 
The `asSource()` extension function converts the byte array into a `Source` object. The `FileSource` class is then instantiated with the file name and the `Source` object.

---
Now you should have a better understanding of how to use the `FileSource` class to pass a file to the OpenAI API. Don't hesitate to ask if you have any further questions or need more clarification.
