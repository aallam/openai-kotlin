package com.aallam.openai.api.engine

import com.aallam.openai.api.engine.internal.EngineIdSerializer
import kotlinx.serialization.Serializable

/**
 * OpenAI’s API engine ID.
 */
@Serializable(EngineIdSerializer::class)
public sealed class EngineId(public val id: String) {

    /**
     * Ada is usually the fastest model and can perform tasks like parsing text, address correction
     * and certain kinds of classification tasks that don’t require too much nuance. Ada’s performance
     * can often be improved by providing more context.
     *
     * Good at: **Parsing text, simple classification, address correction, keywords**
     *
     * *Note: Any task performed by a faster model like Ada can be performed by a more powerful model like Curie or Davinci.*
     */
    public object Ada : EngineId("ada")

    /**
     * Babbage can perform straightforward tasks like simple classification. It’s also quite capable
     * when it comes to Semantic Search ranking how well documents match up with search queries.
     *
     * Good at: **Moderate classification, semantic search classification**
     */
    public object Babbage : EngineId("babbage")

    /**
     * Curie is extremely powerful, yet very fast. While Davinci is stronger when it comes to
     * analyzing complicated text, Curie is quite capable for many nuanced tasks like sentiment
     * classification and summarization. Curie is also quite good at answering questions and
     * performing Q\&A and as a general service chatbot.
     *
     * Good at: **Language translation, complex classification, text sentiment, summarization**
     */
    public object Curie : EngineId("curie")

    /**
     * Davinci is the most capable engine and can perform any task the other models can perform and
     * often with less instruction. For applications requiring a lot of understanding of the content,
     * like summarization for a specific audience and content creative generation, Davinci is going to
     * produce the best results. The trade-off with Davinci is that it costs more to use per API call
     * and other engines are faster.
     *
     * Another area where Davinci shines is in understanding the intent of text.
     * Davinci is quite good at solving many kinds of logic problems and explaining the motives of
     * characters. Davinci has been able to solve some of the most challenging AI problems involving
     * cause and effect.
     *
     * Good at: **Complex intent, cause and effect, summarization for audience**
     */
    public object Davinci : EngineId("davinci")

    /**
     * Can be used to provide an engine id manually.
     */
    public class Custom(id: String) : EngineId(id)

    override fun toString(): String {
        return id
    }
}
