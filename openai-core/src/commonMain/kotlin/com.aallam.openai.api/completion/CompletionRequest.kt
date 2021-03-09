package com.aallam.openai.api.completion

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A request for OpenAI to generate a predicted completion for a prompt.
 * All fields are Optional.
 *
 * [documentation](https://beta.openai.com/docs/api-reference/create-completion)
 */
@Serializable
public data class CompletionRequest(
    /**
     * The prompt(s) to generate completions for, encoded as a string, a list of strings, or a list of token lists.
     *
     * Note that `<|endoftext|>` is the document separator that the model sees during training, so if a prompt is not
     * specified the model will generate as if from the beginning of a new document.
     *
     * Defaults to `<|endoftext|>`.
     */
    @SerialName("prompt")
    public val prompt: String? = null,

    /**
     * The maximum number of tokens to generate.
     * Requests can use up to 2048 tokens shared between prompt and completion.
     * (One token is roughly 4 characters for normal English text)
     *
     * Defaults to 16.
     */
    @SerialName("max_tokens")
    public val maxTokens: Int? = null,

    /**
     * What sampling temperature to use. Higher values means the model will take more risks.
     * Try 0.9 for more creative applications, and 0 (argmax sampling) for ones with a well-defined answer.
     *
     * We generally recommend using this or [topP] but not both.
     *
     * Defaults to 1.
     */
    @SerialName("temperature")
    public val temperature: Double? = null,

    /**
     * An alternative to sampling with temperature, called nucleus sampling, where the model considers the results of
     * the tokens with top_p probability mass. So 0.1 means only the tokens comprising the top 10% probability mass are
     * considered.
     *
     * We generally recommend using this or [temperature] but not both.
     *
     * Defaults to 1.
     */
    @SerialName("top_p")
    public val topP: Double? = null,

    /**
     * How many completions to generate for each prompt.
     *
     * **Note:** Because this parameter generates many completions, it can quickly consume your token quota.
     * Use carefully and ensure that you have reasonable settings for [maxTokens] and [stop].
     *
     * Defaults to 1.
     */
    @SerialName("n")
    public val n: Int? = null,

    /**
     * Whether to stream back partial progress.
     * If set, tokens will be sent as data-only [server-sent events](https://developer.mozilla.org/en-US/docs/Web/API/Server-sent_events/Using_server-sent_events#Event_stream_format)
     * as they become available, with the stream terminated by a data: DONE message.
     *
     * Defaults to `false`.
     */
    @SerialName("stream")
    public val stream: Boolean? = null,

    /**
     * Include the log probabilities on the [logprobs] most likely tokens, as well the chosen tokens.
     * For example, if [logprobs] is 10, the API will return a list of the 10 most likely tokens.
     * The API will always return the [logprob] of the sampled token,
     * so there may be up to [logprobs]+1 elements in the response.
     *
     * Defaults to `null`.
     */
    @SerialName("logprobs")
    public val logprobs: Int? = null,

    /**
     * Echo back the prompt in addition to the completion.
     *
     * Defaults to `false`.
     */
    @SerialName("echo")
    public val echo: Boolean? = null,

    /**
     * Up to 4 sequences where the API will stop generating further tokens.
     * The returned text will not contain the stop sequence.
     *
     * Defaults to `null`.
     */
    @SerialName("stop")
    public val stop: List<String>? = null,

    /**
     * Number between 0 and 1 (default 0) that penalizes new tokens based on whether they appear in the text so far.
     * Increases the model's likelihood to talk about new topics.
     *
     * Defaults to 0.
     */
    @SerialName("presence_penalty")
    public val presencePenalty: Double? = null,

    /**
     * Number between 0 and 1 (default 0) that penalizes new tokens based on their existing frequency in the text so far.
     * Decreases the model's likelihood to repeat the same line verbatim.
     *
     * Defaults to 0.
     */
    @SerialName("frequency_penalty")
    public val frequencyPenalty: Double? = null,

    /**
     * Generates [bestOf] completions server-side and returns the "best"
     * (the one with the lowest log probability per token). Results cannot be streamed.
     *
     * When used with [n], [bestOf] controls the number of candidate completions and [n] specifies how many to return,
     * [bestOf] must be greater than [n].
     *
     * **Note:** Because this parameter generates many completions, it can quickly consume your token quota.
     * Use carefully and ensure that you have reasonable settings for [maxTokens] and [stop].
     *
     * Defaults to 1
     */
    @SerialName("best_of")
    public val bestOf: Int? = null,

    /**
     * Modify the likelihood of specified tokens appearing in the completion.
     *
     * Accepts a json object that maps tokens (specified by their token ID in the GPT tokenizer) to an associated bias`
     * value from -100 to 100. You can use this tokenizer tool (which works for both GPT-2 and GPT-3) to convert text
     * to token IDs. Mathematically, the bias is added to the logits generated by the model prior to sampling.
     * The exact effect will vary per model, but values between -1 and 1 should decrease or increase likelihood
     * of selection; values like -100 or 100 should result in a ban or exclusive selection of the relevant token.
     *
     * As an example, you can pass `{"50256": -100}` to prevent the `<|endoftext|> token from being generated.
     *
     * Defaults to `null`.
     */
    @SerialName("logit_bias")
    public val logitBias: Map<String, Int>? = null,
)
