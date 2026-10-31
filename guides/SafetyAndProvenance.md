# Safety and content provenance

## Existing safety alerts and cases

Retrieve resources already available to the supplied credential:

```kotlin
val alert = openAI.safetyAlert("alert_existing")
val case = openAI.safetyCase("C-existing")
println(alert.errorType)
println(case.notice.type)
```

Alerts belong to the authenticated API project. `requestPaused` reports successful block registration; it does not confirm that response execution stopped. `reason` can be null, including for zero data retention requests. `detailedExplanation` is optional and is only temporarily available for eligible alerts.

Cases retain their affected `entityIdentifier`, notice type and nullable reason. These retrieval operations do not create alerts, open cases or change enforcement decisions. API errors, including HTTP 404, propagate.

## Check an image or audio file

Supply a fresh source and the actual file media type. The SDK sets the multipart boundary and sends one `file` part; the request consumes and closes its source, including on request construction, authentication, transport or cancellation failures. Multipart uploads are sent once without automatic retries.

```kotlin
import com.aallam.openai.api.file.FileSource
import com.aallam.openai.api.provenance.ProvenanceResult
import kotlinx.io.files.Path

val check = openAI.checkContentProvenance(FileSource(Path("image.png")), "image/png")
for (result in check.results) {
    when (result) {
        is ProvenanceResult.C2pa -> println("${result.outcome}: ${result.validationState}, ${result.issuer}")
        is ProvenanceResult.SynthId -> println(result.outcome)
    }
}
```

Images can return C2PA and SynthID results; audio returns SynthID. Inapplicable checks are omitted. Each result retains its own outcome and nullable model/generation-time metadata. Generation times use UTC RFC 3339 text; `createdAt` is the check time in Unix seconds.

`not_detected` means no supported signal was detected. It does not prove human origin or rule out AI generation: editing, metadata removal, watermark degradation, older models and other providers can produce that outcome. A detected signal is evidence of that signal, not a complete file history. An invalid C2PA manifest is not reliable provenance evidence.

Use a supported image/audio media type such as `image/png`, `image/jpeg` or `audio/ogg` for Opus. The current API limits uploads to 50 MiB and decoded audio to 60 seconds. Text verification is separately access-controlled and is not exposed by this image/audio API. Organizations without endpoint access can receive HTTP 404; errors are propagated rather than converted into `not_detected`.

`RequestOptions` supports per-request headers, query parameters and timeouts on all three operations. Custom clients inherit default unsupported implementations until they provide these capabilities.

See the official [provenance guide](https://developers.openai.com/api/docs/guides/content-provenance), [alert reference](https://developers.openai.com/api/reference/resources/safety/subresources/alerts/methods/retrieve) and [case reference](https://developers.openai.com/api/reference/resources/safety/subresources/cases/methods/retrieve) for current availability and resource semantics.
