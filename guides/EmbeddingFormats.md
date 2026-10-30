# Embedding input and vector formats

The existing `EmbeddingRequest` and `EmbeddingResponse` API remains available with
text-list inputs and numeric vectors. Use the `EmbeddingCreateRequest` overload for
single text, text batches, token-ID arrays, token-ID batches, or base64 output:

```kotlin
val result = openAI.embeddings(
    EmbeddingCreateRequest(
        model = ModelId("text-embedding-3-small"),
        input = EmbeddingInput.TokenBatch(listOf(listOf(12, 34), listOf(56))),
        encodingFormat = EmbeddingEncodingFormat.Base64,
    )
)
val coordinates: List<Double> = result.embeddings.first().embedding.values()
val modelUsed = result.model
```

`EmbeddingInput.Text`, `Texts`, `Tokens`, and `TokenBatch` serialize to the API's
unwrapped string/array shapes. The API's model token limits still apply. Omitting
`encodingFormat` preserves the server default; numeric JSON output retains its
original double precision.

`EmbeddingVector.Encoded` retains the returned base64 string. `values()` explicitly
decodes its little-endian IEEE 754 float32 coordinates into doubles. Invalid base64
or a decoded byte count not divisible by four throws `IllegalArgumentException`.
Results preserve both response and per-vector object metadata, input indexes,
model ID, and token usage.
