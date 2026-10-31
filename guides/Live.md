# Live

Live manages spoken conversations with an application, Responses or Decisions backend. The SDK exposes media-session creation and controls, recording download, and scoped primary, sideband and stored-fork WebSockets. Device capture/playback, WebRTC peer connections and SIP provider integration remain caller-owned.

## Primary WebSocket

`liveSession` connects to `/live/sessions` without a model query parameter and sends `session.start` using the supplied configuration. Wait for `session.started` before audio or other application commands. Keep one event collector active throughout the connection.

This minimal lifecycle example starts a session and closes it without sending audio or delegated work:

```kotlin
import com.aallam.openai.api.live.*
import com.aallam.openai.api.model.ModelId
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.takeWhile

openAI.liveSession(LiveSessionConfig(
    model = ModelId("gpt-live-1"),
    delegation = LiveDelegation.Client(),
)) {
    events.takeWhile { event ->
        when (event.type) {
            "session.started" -> send(LiveEvent.closeSession())
            "session.closed" -> println("Live seconds: ${event.usage?.seconds}")
            "error" -> error(event.error?.message ?: "Live protocol error")
        }
        event.type != "session.closed"
    }.collect()
}
```

For audio, configure `LiveAudioConfig(format = LiveAudioFormat())`, then send `LiveEvent.appendAudio(base64Pcm)` after startup. PCM is raw mono 16-bit little-endian data at 16000 or 24000 Hz, without a WAV header. G.711 `audio/pcmu` and `audio/pcma` use 8000 Hz. Decode `session.output_audio.delta` in the configured format. Capture/playback and base64 conversion belong to your application.

`LiveEvent` exposes common lifecycle, transcript, audio, timing, error and usage fields while retaining its complete original `payload`. A `decision.event` exposes the native typed `Decision`; a `response.event` retains its nested Responses event. Service errors remain protocol events so the application can decide whether to continue. Raw JSON supports evolving nested event variants without discarding unknown fields.

`appendInstructions`, `appendThinking` and `appendCommentary` preserve the required nullable `delegation_id`. Supply a delegation ID when returning work for a client delegation, or omit the helper argument for general context. Responses tool results can be appended with `createResponseItem`, followed by `createResponse`. Decision items can be appended/deleted without triggering an evaluation; use `createDecision` with a unique event ID to evaluate them.

## WebRTC and SIP

Create a WebRTC session by exchanging an SDP offer:

```kotlin
val created = openAI.createLiveSession(liveSessionRequest {
    session = LiveSessionConfig(ModelId("gpt-live-1"), store = true)
    transport = LiveTransport.WebRtc(offerSdp)
})
val answerSdp = (created.transport as LiveTransportResult.WebRtc).sdp
val sessionId = created.session.id
```

The caller applies the answer to its peer connection. Media transports negotiate their format; omit `audio.format`. Incoming SIP calls can be accepted, rejected with status 300 through 699, transferred to a SIP/tel URI, or hung up using the corresponding `*LiveSession` operations.

`LiveTransport.Sip` places an actual outbound call using `LiveSipTrunk` and `LiveSipAuthentication`. It requires provider access and organization eligibility. SIP creation disables automatic SDK request retries because an uncertain response may already have placed a call. Trunk passwords are redacted from object `toString`, but remain in the request body; do not enable body logging for those requests. No automatic live tests dial external phone numbers.

## Sidebands, stored forks and recordings

`attachLiveSession(id)` connects to `/live/sessions/{id}/attach` without a startup command or event replay. Use it to monitor/control a session whose primary transport is elsewhere. Input audio belongs to the primary transport; a sideband receives reflected audio and should not send `session.input_audio.append`. Closing a sideband closes that connection, not the underlying call.

Use `store = true` at startup to allow later recording downloads and forking. `downloadLiveRecording(id)` preserves the recording's binary bytes. `forkLiveSession(id, LiveSessionForkRequest(...))` exchanges a new WebRTC offer; `forkLiveConnection(id, overrides)` opens `/live/sessions/{id}/fork` and sends `session.start` with overrides. An empty override object inherits stored settings. Forks inherit the model and voice; a WebSocket fork may choose a new audio format.

`LiveClientConfig` exposes frontend data-channel permissions, and `LiveDelegation` selects application, Responses or Decisions ownership. Backend Responses tools/tool-choice shapes retain existing SDK JSON escape hatches. `LiveSessionOverrides.delegation.responses` preserves explicit null and evolving Responses update fields through JSON. Session updates can likewise use `LiveEvent.updateSession` with an explicit JSON object.

## Lifetime and usage

Each scoped connection closes its socket on normal return, an exception, cancellation, an initial-send failure, or early termination of event collection. To obtain final usage, send `session.close` and collect through `session.closed` before leaving the scope. Keep a single collector; calling `first()` or `take(1)` intentionally closes that connection afterward.

Live is billed by session duration, including a session with no audio. `LiveUsage.seconds` is cumulative and must not be summed over events. Backend Responses/Decisions usage is separate. The optional live test starts and immediately closes one client-delegated session, with a ten-second deadline and no audio, storage or SIP call.

The server WebSocket transport uses the selected Ktor engine and backend bearer authentication. Browser engine/header limitations are the same as the documented [Realtime transport](Realtime.md); `secure = false` is intended only for trusted local test servers. See the official [Live guide](https://developers.openai.com/api/docs/guides/live) and [resource reference](https://developers.openai.com/api/reference/resources/live/methods/create) for current limits and platform availability.

## Local live test

The strict JVM connection fixture is separate from the ordinary live suite because Live availability depends on the project. With an eligible key in `OPENAI_API_KEY`, run `OPENAI_LIVE_API_TESTS=1 ./gradlew :openai-client:liveApiJvmTest`. It opens and closes one primary connection without audio input, outbound SIP calls or storage.
