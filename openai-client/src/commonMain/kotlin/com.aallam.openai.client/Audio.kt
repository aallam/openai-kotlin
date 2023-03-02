package com.aallam.openai.client

import com.aallam.openai.api.BetaOpenAI

/**
 * Learn how to turn audio into text.
 */
public interface Audio {

    /**
     * Transcribes audio into the input language.
     */
    @BetaOpenAI
    public fun transcriptions()

    /**
     * Translates audio into English.
     */
    @BetaOpenAI
    public fun translations()
}
