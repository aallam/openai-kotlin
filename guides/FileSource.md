# 📁 `FileSource` 

`FileSource` is a class designed to represent a file resource in the OpenAI API. It allows you to pass a file to the API using two different methods:
1. By providing the file path and the corresponding file system.
2. By providing the file name and its Source.

In this documentation, we will explain both methods in detail.

## Method 1: Using the file path and the corresponding file system

To use this method, you need to provide the file path as a `Path` object and the corresponding `FileSystem`.

### Usage
```kotlin
import com.aallam.openai.api.file.FileSource
import okio.FileSystem
import okio.Path.Companion.toPath

val fileSource = FileSource(path = "your-file-path".toPath(), fileSystem = FileSystem.YOUR_FILE_SYSTEM)
```

### Example
```kotlin
import com.aallam.openai.api.file.FileSource
import okio.FileSystem
import okio.Path.Companion.toPath

fun main() {
    // Define the file path to your local audio file
    val filePath = "path/to/your/local/audio-file.wav"

    // Create a Path instance for the local audio file
    val path = filePath.toPath()

    // Create a FileSource instance using the file path 
    // and the file system (FileSystem.SYSTEM, FileSystem.RESOURCES..etc.)
    val fileSource = FileSource(path = path, fileSystem = FileSystem.SYSTEM)

    // Use the FileSource instance in your application
    // ...
}
```

In this example, we first define the file path to the local file in the `filePath` variable. We then create a `Path` instance for the local file by calling the `toPath()` extension function from the `okio.Path` companion object.

Next, we create a `FileSource` instance by providing the `Path` instance (`path`) and the local file system (`FileSystem.SYSTEM`). This is done by passing these values as arguments to the `FileSource` constructor.

Finally, the `FileSource` instance can be used in your application as needed.

## Method 2: Using the file name and its source

To use this method, you need to provide the file name as a `String` and the corresponding `Source`.

### Usage
```kotlin
import com.aallam.openai.api.file.FileSource
import okio.asSource

val fileSource = FileSource(name = "your-file-name", source = yourByteArray.asSource())
```

### Example
```kotlin
import com.aallam.openai.api.file.FileSource
import okio.FileSystem
import okio.Path.Companion.toPath

fun main() {
    // Define the file path to your local audio file
    val filePath = "path/to/your/local/audio-file.wav"

    // Create a Path instance for the local audio file
    val path = filePath.toPath()

    // Read the local audio file using the file system 
    // (FileSystem.SYSTEM, FileSystem.RESOURCES..etc.)
    val audioSource = FileSystem.SYSTEM.source(path)

    // Create a FileSource instance using the audio file
    val fileSource = FileSource(name = "audio-file.wav", source = audioSource)

    // Use the FileSource instance in your application
    // ...
}
```

In this example, we first define the file path to the local file in the `filePath` variable. We then create a `Path` instance for the local audio file by calling the `toPath()` extension function from the `okio.Path` companion object. The `audioSource` variable is assigned the result of reading the local audio file using the `FileSystem.SYSTEM.source(path)` function.

Finally, we create a `FileSource` instance by providing the file name (in this case, "audio-file.wav") and the `Source` object (`audioSource`). This `FileSource` instance can then be used in your application as needed.

## Links
- [Okio Sources and Buffers](https://square.github.io/okio/#sources-and-sinks)
- [Okio FileSystem](https://square.github.io/okio/file_system/)
