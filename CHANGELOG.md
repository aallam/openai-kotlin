# 3.3.0
> Published 19 Jun 2023

### Added
- **Chat**: Function Call (#200 #202) (thanks @JochenGuckSnk)

# 3.2.5
> Published 03 Jun 2023

### Added
- Add `OpenAIHost` builder for Azure (#196)
- Support of `OpenAIHost` with a base path (#196)

# 3.2.4
> Published 21 May 2023

### Added
- Logging configuration using `LoggingConfig`
- `sanitize` flag to sanitize authorization header in the logs
- Enhance `OpenAI()` factory function parameters

### Changed
- Updated `OpenAIConfig` to replace logging fields with `LoggingConfig`
- Authorization token is now hidden by default in the logs

# 3.2.3
> Published 01 May 2023

### Added
- `OpenAIIOException` and `GenericIOException` (#178)

# 3.2.2
> Published 29 Apr 2023

### Feat
- **Completion**: add suffix request param (#174)
- **Exceptions**: Add more detailed api errors (#163)

### Change
- Replace `Autocloseable` until stable (#173)

# 3.2.1
> Published 9 Apr 2023

### Added
- Proguard / R8 rules for jvm (#149)
- `OpenAI` implements `AutoCloseable` (#151)

### Dependencies
- Kotlin to `1.8.20` (#146)
- Kotlin serialization to `1.5.0` (#146)
- Ktor to `2.2.4` (#146)

# 3.2.0
> Published 23 Mar 2023

### Added
- **Audio**: add other formats support (#127)
- **Chat**: add user param to chat request (#128)

### Changed
- Simplify and add more exceptions (#123)

# 3.1.1
> Published 17 Mar 2023

### Changed
- Add error details to `OpenAIAPIException` (#120) (thanks @rosuH)

### Fixed
- **Audio**: switch mixed-up translation and transcription paths (#119) (thanks @matusekma)

# 3.1.0
> Published 10 Mar 2023

### Added
- **Audio**: api implementation (#105)
- **Config**: proxy config (#111)
- **Config**: retry strategy config (#112)

# 3.0.0
> Published 02 Mar 2023

**Important changes since 2.1.3**

### Added
- **Requests**: DSL builder functions to create requests (#80)
- **Completions** and `Edit`: add `usage` to the responses (#82)
- **Files**: add `download` and `delete` functions (#86)
- **OpenAIConfig**: `headers` and `organization` (#96)
- **Embeddings**: `similarity` and `distance` (#88) *(experimental)*
- **Chat** implementation (#99) *(beta)*
- **OpenAIConfig**: custom host config (#102)

### Changed
- **FileSource** and [okio](https://square.github.io/okio/) for I/O operations (e.g. files, images) (#75)
- **FineTunes**: remove experimental (#90)
- **Images**: mark as experimental (#91)
- **Embeddings**: response changed to `EmbeddingResponse` to include `usage`. (#82)

### Fixed
- Non-blocking SSE `Flow` events (#95)

### Dependencies
- Update Kotlin to `1.8.10`

# 3.0.0-beta02
> Published 19 Feb 2023

### Added
- `OpenAIConfig`: `headers` and `organization` (#96)

### Changed
- `FineTunes`: remove experimental (#90)
- `Images`: mark as experimental (#91)

### Fixed
- Non-blocking SSE `Flow` events (#95)

# 3.0.0-beta01
> Published 06 Feb 2023

This release contains changes from [3.0.0-alpha01](#300-alpha01), plus the following:

### Added
- Embeddings: similarity and distance (#88) (Experimental)

### Changed
- Update Kotlin to `1.8.10`

# 2.1.3
> Published 29 Jan 2023

### Fixed
- Add darwin simulators and x86 targets (#85)

# 3.0.0-alpha01
> Published 20 Jan 2023

### Added
* DSL builder functions to create requests
* `Completions` and `Edit`: add `usage` to the responses
* `Files`: add `download` and `delete` functions

### Changed
* `FileSource` and [okio](https://square.github.io/okio/) for I/O operations (e.g. files, images).
* `Embeddings`: response changed to `EmbeddingResponse` to include `usage`.

# 2.1.2
> Published 11 Jan 2023

### Fixed

- FineTunes: events streaming using `GET` (#76) (thanks @PatrickLaflamme)

# 2.1.1
> Published 02 Jan 2023

### Added
* Targets `ios`, `watchos` and `tvos` _(x64/arm64)_

### Changed
* Update Kotlin to `1.8.0`

# 2.1.0
> Published 14 Dec 2022

### Added
* [Images](https://beta.openai.com/docs/api-reference/images) implementation (#68)
* Http timeout configuration (#71)

### Changed
* Update Kotlin to `1.7.20`
* Update Kotlin serialization to `1.4.1`
* Update Ktor `2.1.3`

# 2.0.0
> Published 20 Aug 2022

### Added
* [Models](https://beta.openai.com/docs/api-reference/models) implementation
* [Moderations](https://beta.openai.com/docs/api-reference/moderations) implementation
* [Fine-Tunes](https://beta.openai.com/docs/api-reference/fine-tunes) implementation

### Changed
* Update Kotlin to `1.7.10`
* Update Kotlin serialization to `1.4.0`
* Update `Completions`, `Edits` and `Embeddings` to use `Models`

### Removed
* `Engines`, `Answers`, `Search` and `Classification`

# 1.2.0
> Published 11 Jun 2022

### Added
* `openai-client-bom` artifact for jvm projects

### Changed
* Update Kotlin to `1.7.0` (#31)
* Update Coroutines to `1.6.2` (#26)

### Deprecated
* Answers, Classification and Search Endpoints (#29)

# 1.1.0
> Published 08 May 2022

### Added
* Field `user` to `CompletionRequest` (#9) (thanks @Stuie)
* Native targets: `linuxX64`, `macosX64`, `macosArm64` and `mingwX64` (w/ new memory manager)

### Changed
* Kotlin `1.6.21`
* Kotlin coroutines `1.6.1`
* The client throws `OpenAIException` on errors

# 1.0.0
> Published 19 Jun 2021

### Changed
* Kotlin 1.5.10
* Value classes: `EngineID`, `FileStatus`, `FileId` and `Purpose`  

# 0.5.0
> Published 03 Apr 2021

### Added
* [Files](https://beta.openai.com/docs/api-reference/files)

### Changed
* Kotlin `1.4.32`

### Experimental  
* [Classifications](https://beta.openai.com/docs/api-reference/classifications)
* [Answers](https://beta.openai.com/docs/api-reference/answers)

# 0.4.0
> Published 20 Mar 2021

### Changed
* Deprecate `stream` parameter in `CompletionRequest`.

# 0.3.0
> Published 13 Mar 2021

### Changed
* Remove Ktor's logging classes from public API

# 0.2.0
> Published 11 Mar 2021

### Added
* `OpenAI.completions`: text completions as stream of events

### Fixed
* `OpenAI.completion` when `stream` is set

### Changed
* `OpenAI.createCompletion` renamed to `OpenAI.completion`

# 0.1.1
> Published 10 Mar 2021

### Changed
* Ktor as an implementation dependency of `openai-client`

# 0.1.0
> Published 09 Mar 2021

### Added
* [List engines](https://beta.openai.com/docs/api-reference/list-engines)
* [Retrieve engine](https://beta.openai.com/docs/api-reference/retrieve-engine)
* [Create completion](https://beta.openai.com/docs/api-reference/create-completion)
* [Search](https://beta.openai.com/docs/api-reference/search)
