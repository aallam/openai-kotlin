# FileSource

`FileSource` represents a file payload uploaded to OpenAI APIs.

You can create it in two ways:

1. From a `Path` (recommended)
2. From a custom `RawSource`

## 1) From a Path

Use this when the file already exists on disk.

```kotlin
import com.aallam.openai.api.file.FileSource
import kotlinx.io.files.Path

val fileSource = FileSource(path = Path("path/to/audio.wav"))
```

By default, this uses `SystemFileSystem`.

You can pass a custom filesystem when needed:

```kotlin
import com.aallam.openai.api.file.FileSource
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem

val fileSource = FileSource(
    path = Path("path/to/audio.wav"),
    fileSystem = SystemFileSystem
)
```

## 2) From a custom source

Use this when you already have a `RawSource` and want to control the uploaded file name.

```kotlin
import com.aallam.openai.api.file.FileSource
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem

val source = SystemFileSystem.source(Path("path/to/audio.wav"))
val fileSource = FileSource(name = "audio.wav", source = source)
```

## Example with File Upload

```kotlin
val uploaded = openAI.file(
    request = FileUpload(
        file = FileSource(path = Path("training.jsonl")),
        purpose = Purpose("fine-tune")
    )
)
```
