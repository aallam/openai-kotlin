# Troubleshooting Guide

Facing issues while using the OpenAI Kotlin API Client? Here are common checks.

<details>
  <summary>"The model ... does not exist" or "You do not have access to model ..."</summary>

1. Confirm the model ID is valid for your account.
2. Call `openAI.models()` and verify the model appears in your list.
3. If you use Azure, make sure `deploymentId` matches your deployed model and that `apiVersion` is supported.
</details>

<details>
  <summary>401 Unauthorized / invalid_api_key</summary>

1. Verify `OPENAI_API_KEY` is set in the environment used by your runtime.
2. Ensure there are no extra spaces/newlines in the key value.
3. If using custom hosts or proxies, confirm the `Authorization` header is being forwarded unchanged.
</details>

<details>
  <summary>429 Rate limit or quota errors</summary>

1. Retry with exponential backoff.
2. Reduce concurrency and token usage.
3. Tune `RetryStrategy` in `OpenAIConfig` for your traffic pattern.
</details>

<details>
  <summary>Live tests fail unexpectedly</summary>

`openai-client` live tests are opt-in and billable. Ensure:

1. `OPENAI_LIVE_TESTS=1`
2. `OPENAI_API_KEY` is set
3. The selected model/feature is available to your account
</details>
