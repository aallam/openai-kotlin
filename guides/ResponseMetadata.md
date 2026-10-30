# Response metadata

Pass an `onResponse` callback through `RequestOptions` to observe HTTP status and response headers while keeping the normal return type.

```kotlin
var metadata: ResponseMetadata? = null
val model = openAI.model(
    ModelId("your-model"),
    RequestOptions(onResponse = { metadata = it }),
)
println(metadata?.requestId)
println(metadata?.rateLimitRemainingRequests)
```

The same option works on streaming requests. The callback runs when the response arrives, before decoding the body or consuming stream events. It receives HTTP error responses before the usual API exception is thrown, and still runs if subsequent body decoding fails. A transport failure or cancellation before receiving a response does not invoke it.

`ResponseMetadata` exposes:

- `statusCode` and a snapshot of all response `headers`.
- Case-insensitive `header(name)` and `headerValues(name)` access, including repeated header values.
- `requestId`, request/token limits, remaining counts, and reset durations when the server supplies them.

Missing headers produce null convenience values. Numeric limits that cannot be parsed also produce null; the original header remains accessible. Reset durations retain the server's string format.

Callbacks run inline in request processing and should avoid blocking. Throwing from the callback fails the operation without resending the request; coroutine cancellation remains cancellation. Retries or redirects can produce multiple responses and callbacks for a single operation. Existing calls without `onResponse` are unchanged.
