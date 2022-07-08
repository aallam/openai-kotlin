# 2.0.0-beta01
> Published 08 Jul 2022

### Added
* [Models](https://beta.openai.com/docs/api-reference/models) implementation

### Changed
* Update Kotlin to `1.7.10`
* Update Completions, Edits and Embeddings to use Models

### Removed
* Engines, Answers, Search and Classification

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
