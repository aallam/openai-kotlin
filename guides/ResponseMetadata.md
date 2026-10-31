# Response metadata

Configure an `onResponseMetadata` observer in the client's existing HTTP configuration hook to observe HTTP status and response headers while keeping the normal return type.

```kotlin
import com.aallam.openai.client.onResponseMetadata

val openAI = OpenAI(token = token) {
    onResponseMetadata { metadata ->
        println(metadata.requestId)
        println(metadata.rateLimitRemainingRequests)
    }
}
val model = openAI.model(ModelId("your-model"))
```

The observer applies to every request made by this client, including streaming requests. With `OpenAIConfig`, use the same hook: `OpenAIConfig(token = token) { onResponseMetadata { metadata -> /* observe */ } }`.

The callback runs when the response arrives, before decoding the body or consuming stream events. It receives HTTP error responses before the usual API exception is thrown, and still runs if subsequent body decoding fails. A transport failure or cancellation before receiving a response does not invoke it.

`ResponseMetadata` exposes:

- `statusCode` and a snapshot of all response `headers`.
- Case-insensitive `header(name)` and `headerValues(name)` access, including repeated header values.
- `requestId`, request/token limits, remaining counts, and reset durations when the server supplies them.

Missing headers produce null convenience values. Numeric limits that cannot be parsed also produce null; the original header remains accessible. Reset durations retain the server's string format.

Callbacks run inline in request processing and should avoid blocking. Throwing from the callback fails the operation without resending the request; coroutine cancellation remains cancellation. Retries or redirects can produce multiple responses and callbacks for a single operation. Concurrent requests may invoke the observer concurrently, so coordinate shared state. Observation is disabled unless configured; `RequestOptions` and endpoint return types are unchanged.
