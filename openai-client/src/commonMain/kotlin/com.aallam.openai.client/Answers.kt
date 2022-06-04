package com.aallam.openai.client

import com.aallam.openai.api.ExperimentalOpenAI
import com.aallam.openai.api.answer.Answer
import com.aallam.openai.api.answer.AnswerRequest

/**
 * Given a question, a set of documents, and some examples, the API generates an answer to the question based on
 * the information in the set of documents. This is useful for question-answering applications on sources of truth,
 * like company documentation or a knowledge base.
 */
@Deprecated("Answers APIs are deprecated")
public interface Answers {

    /**
     * Answers the specified question using the provided documents and examples.
     *
     * The endpoint first searches over provided documents or files to find relevant context.
     * The relevant context is combined with the provided examples and question to create the prompt for [completion].
     */
    @Deprecated("Answers APIs are deprecated")
    @ExperimentalOpenAI
    public suspend fun answers(request: AnswerRequest): Answer
}
