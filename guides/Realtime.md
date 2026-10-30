# Realtime audio and call signaling

The client supports the GA Realtime protocol. It does not send the retired `OpenAI-Beta: realtime=v1` header.

## Supported scope

- Create ephemeral credentials for conversation, transcription and continuous translation sessions.
- Configure nested GA audio settings and send/receive JSON WebSocket events, including audio chunks, response deltas, function results, approvals and future events through their preserved payloads.
- Exchange a caller-provided WebRTC SDP offer for the server answer and call Location.
- Accept, reject, transfer and hang up incoming SIP calls.
- Open a model-based conversation, an existing call's sideband, or a continuous translation WebSocket connection.

The caller owns audio capture/playback, codecs, base64 encoding, a WebRTC peer connection and any SIP carrier integration. This library supplies signaling and protocol transport, not device media stacks. Use a Ktor engine that supports WebSockets. JVM and Native engines can supply authorization headers; browser WebSockets cannot generally supply arbitrary headers. Browser applications should use WebRTC with a short-lived client secret rather than exposing a permanent API key. Target compilation does not establish engine support for a particular browser or native engine.

## Credentials and sessions

```kotlin
val credential = openAI.realtimeClientSecret(
    RealtimeClientSecretRequest(
        session = RealtimeSessionConfig(model = ModelId("gpt-realtime-2.1")),
        expiresAfter = RealtimeSecretExpiry(seconds = 60),
    ),
    requestOptions = RequestOptions(headers = mapOf("OpenAI-Safety-Identifier" to hashedUserId)),
)
```

Return `credential.value` only to the intended client. Do not log or persist ephemeral credentials. Expiry limits creation of new sessions; it does not terminate an already established session.

For a trusted server WebSocket, configure `OpenAIConfig.token` with the appropriate API key or ephemeral credential. Session defaults can also be updated using `RealtimeEvent.updateSession`; JSON null explicitly disables supported controls, while omitted fields preserve server defaults.

```kotlin
openAI.realtimeSession(model = ModelId("gpt-realtime-2.1")) {
    send(RealtimeEvent.appendAudio(base64PcmAudio))
    send(RealtimeEvent.commitAudio()) // Manual turns only, when automatic turn detection is disabled.
    send(RealtimeEvent.createResponse())
    events.collect { event ->
        when (event.type) {
            "response.output_audio.delta" -> playAudio(event.delta)
            "response.output_text.delta" -> appendText(event.delta)
            "error" -> handleServiceError(event.error)
        }
    }
}
```

Send audio and collect events concurrently when continuous input is needed. `events` has one consumer. Leaving the session block, cancelling the block, or stopping event collection closes the socket. Service errors remain protocol events; connection/protocol failures throw. The API does not reconnect or replay audio automatically.

`RealtimeEvent.functionOutput(callId, output)` supplies a tool result; send a response-create event afterward if another response is required. `RealtimeEvent.payload` retains complete unknown/tool-specific events, including arguments, MCP approvals, item details, usage and tracing. The client does not execute tools or approve remote actions automatically.

## WebRTC and SIP

```kotlin
val call = openAI.realtimeCall(sdpOffer, RealtimeSessionConfig(model = ModelId("gpt-realtime-2.1")))
peerConnection.setRemoteDescription(call.sdp)
// Use call.id for sideband control when Location was returned.
openAI.realtimeSession(callId = requireNotNull(call.id)) { /* send or collect events */ }
```

Incoming SIP call IDs come from the call webhook. Configure `acceptRealtimeCall`, `rejectRealtimeCall`, `referRealtimeCall` and `hangupRealtimeCall` in your own authorized webhook handling. A `RealtimeCall` preserves both the SDP answer and Location rather than assuming a JSON response.

## Continuous translation

Create credentials with `realtimeTranslationSecret(RealtimeTranslationSecretRequest(...))`. Its audio configuration uses translation-specific `input.noise_reduction`, `input.transcription`, and `output.language` fields. Connect with `realtimeTranslation(model = ...)`; the model is a required handshake query parameter and cannot be changed during the session. WebSocket input is base64-encoded, 24 kHz PCM16 mono little-endian raw audio. Send append-audio events and consume translation audio/transcript events from the preserved event payload. Send `RealtimeEvent.closeTranslation()` to request graceful completion.

Sources: [Realtime guide](https://developers.openai.com/api/docs/guides/realtime), [client secrets](https://developers.openai.com/api/reference/resources/realtime/subresources/client_secrets/methods/create), [call signaling](https://developers.openai.com/api/reference/resources/realtime/subresources/calls/methods/create), [translation connection](https://developers.openai.com/api/reference/resources/realtime/subresources/translations/methods/connect).
