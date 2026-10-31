# Custom voice management

Use `createVoice` to save a voice from either a text description or a consented reference recording. The SDK also manages consent recording metadata and pagination. Creating and deleting resources requires the appropriate project access and voice permissions.

## Create a prompt-generated voice for Live

```kotlin
import com.aallam.openai.api.voice.*

val voice = openAI.createVoice(promptVoiceRequest {
    name = "Support narrator"
    prompt = "A calm, warm fictional narrator with a clear and measured delivery."
    model = "auto"
})
```

Prompt-generated voices work with Live. They are not supported by the speech, Realtime or Chat Completions APIs. Each creation saves a new voice, and the response contains metadata rather than preview audio. `model` and `scriptHint` can be omitted to use server defaults.

## Create a voice from the speaker's recordings

Provide a recording of the speaker reading an approved consent phrase, then a separate reference sample from that same speaker. Recording-based creation is restricted to eligible customers. Consult the [custom voices guide](https://developers.openai.com/api/docs/guides/custom-voices) for current phrases, recording requirements, limits and availability.

```kotlin
import com.aallam.openai.api.file.FileSource
import kotlinx.io.files.Path

val consent = openAI.createVoiceConsent(voiceConsentRequest {
    name = "Speaker consent"
    language = "en-US"
    recording = FileSource(Path("consent.wav"))
})

val voice = openAI.createVoice(sampleVoiceRequest {
    name = "Support voice"
    this.consent = consent.id
    audioSample = FileSource(Path("reference.webm"))
    mediaType = "audio/webm"
})
```

Audio bytes are uploaded unchanged. When `mediaType` is omitted, the SDK infers a supported audio MIME type from the filename. Set it explicitly when the filename has no recognized audio extension. MIME codec parameters are stripped, so `audio/webm;codecs=opus` is uploaded as `audio/webm`. Sample and consent uploads disable automatic retries because their `FileSource` streams are consumed once. These sources close on success, local validation failure, HTTP failure or cancellation; open a fresh source for another request.

Recording-based voices can be selected through the existing custom-voice reference, such as `CustomVoice(voice.id)` in speech options. Voice selection is independent of enrollment.

## Consent metadata, pages and deletion

```kotlin
val metadata = openAI.voiceConsent(consent.id)
val renamed = openAI.updateVoiceConsent(consent.id, voiceConsentUpdateRequest {
    name = "Updated consent label"
})
val page = openAI.voiceConsents(limit = 20)
val next = if (page.hasMore) openAI.voiceConsents(after = page.lastId, limit = 20) else null

val voiceDeleted = openAI.deleteVoice(voice.id)
val consentDeleted = openAI.deleteVoiceConsent(consent.id)
```

Updates change only the consent label, not the recording or language. `VoiceConsentPage` preserves the list envelope, `hasMore`, and nullable first/last cursors. Retrieving an absent consent returns `null`; deleting an absent voice or consent returns `false`. Other API failures retain their normal exception types. Deleting a voice does not delete its consent recording.

There is no SDK voice-list or voice-retrieve operation because those operations are absent from the current resource reference. Custom client implementations can inherit the interface's default unsupported operations while adding voice support incrementally.
