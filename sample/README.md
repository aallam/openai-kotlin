# Sample App

JVM, JS and Native sample apps.

### Running the apps

| Target | command                                               |
|--------|-------------------------------------------------------|
| jvm    | `./gradlew :sample:jvm:run`                           |
| js     | `./gradlew :sample:js:jsNodeDevelopmentRun`           |
| native | `./gradlew :sample:native:runReleaseExecutableNative` |

Running the apps sends live API requests and may incur charges. Compilation does not run them.

Set `OPENAI_API_KEY` before running. The JS and Native examples also require `OPENAI_MODEL` to name a supported Chat Completions model; they use that same model for retrieval and chat requests.

`OPENAI_IMAGE_MODEL` selects the image generation/editing model and defaults to `gpt-image-2.5-sunburst`. The examples request one low-quality 1024x1024 PNG and use uploaded-file references for edits, deleting the uploaded input afterward. The JVM example saves decoded PNG files; JS and Native report decoded byte counts. None prints the full base64 image payload. Choose a GPT Image model supporting these parameters when overriding the default.

The audio translation example intentionally demonstrates the legacy `audio/translations` contract with `whisper-1`; that endpoint is scheduled to retire on 2027-02-26. Its model cannot be replaced with an arbitrary transcription or Chat model. Use the current [live translation workflow](https://developers.openai.com/api/docs/guides/realtime-translation) for new translation integrations.
