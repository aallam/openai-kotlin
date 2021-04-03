package com.aallam.openai.api.file


public data class FileRequest(

        /**
         * JSON Lines file to be uploaded.
         *
         * If the [purpose] is set to "search" or "answers", each line is a JSON record with a "text" field and an optional
         * "metadata" field. Only "text" field will be used for search. Specially, when the purpose is "answers", "\n"
         * is used as a delimiter to chunk contents in the "text" field into multiple documents for finer-grained matching.
         *
         * If the [purpose] is set to "classifications", each line is a JSON record with a single training example with "text"
         * and "label" fields along with an optional "metadata" field.
         */
        public val file: String,

        /**
         * The intended purpose of the uploaded documents.
         *
         * Use "search" for searches, "answers" for answers and "classifications" for classifications.
         * This allows us to validate the format of the uploaded file.
         */
        public val purpose: Purpose,
)
