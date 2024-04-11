package com.aallam.openai.azure.api.filtering

import com.aallam.openai.azure.api.core.ResponseError
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Information about content filtering evaluated against generated model output.
 */
@Serializable
public data class ContentFilterResultsForChoice(

    /**
     * Describes language related to anatomical organs and genitals, romantic relationships,
     * acts portrayed in erotic or affectionate terms, physical sexual acts, including
     * those portrayed as an assault or a forced sexual violent act against one’s will,
     * prostitution, pornography, and abuse.
     */
    @SerialName("sexual")
    val sexual: ContentFilterResult? = null,

    /**
     * Describes language related to physical actions intended to hurt, injure, damage, or
     * kill someone or something; describes weapons, etc.
     */
    @SerialName("violence")
    val violence: ContentFilterResult? = null,

    /**
     * Describes language attacks or uses that include pejorative or discriminatory language
     * with reference to a person or identity group on the basis of certain differentiating
     * attributes of these groups including but not limited to race, ethnicity, nationality,
     * gender identity and expression, sexual orientation, religion, immigration status, ability
     * status, personal appearance, and body size.
     */
    @SerialName("hate")
    val hate: ContentFilterResult? = null,

    /**
     * Describes language related to physical actions intended to purposely hurt, injure,
     * or damage one’s body, or kill oneself.
     */
    @SerialName("self_harm")
    val selfHarm: ContentFilterResult? = null,

    /**
     * Describes whether profanity was detected.
     */
    @SerialName("profanity")
    val profanity: ContentFilterDetectionResult? = null,

    /**
     * Describes detection results against configured custom blocklists.
     */
    @SerialName("custom_blocklists")
    val customBlocklists: List<ContentFilterBlocklistIdResult>? = null,

    /**
     * Describes an error returned if the content filtering system is
     * down or otherwise unable to complete the operation in time.
     */
    @SerialName("error")
    val error: ResponseError? = null,

    /**
     * Information about detection of protected text material.
     */
    @SerialName("protected_material_text")
    val protectedMaterialText: ContentFilterDetectionResult? = null,

    /**
     * Information about detection of protected code material.
     */
    @SerialName("protected_material_code")
    val protectedMaterialCode: ContentFilterCitedDetectionResult? = null
)