package com.aallam.openai.api.responses

import com.aallam.openai.api.core.Parameters
import com.aallam.openai.api.vectorstore.VectorStoreId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public sealed interface ResponseTool {

    /**
     * Function tool for function calling
     */
    @Serializable
    @SerialName("function")
    public data class Function(
        /**
         * The name of the function to be called. Must be a-z, A-Z, 0-9, or contain underscores and dashes, with a maximum
         * length of 64.
         */
        @SerialName("name") val name: String,

        /**
         * The parameters the functions accept, described as a JSON Schema object.
         * See the [guide](https://platform.openai.com/docs/guides/text-generation/function-calling) for examples,
         * and the [JSON Schema reference](https://json-schema.org/understanding-json-schema) for documentation about
         * the format.
         *
         * Omitting `parameters` defines a function with an empty parameter list.
         */
        @SerialName("parameters") val parameters: Parameters? = null,

        /**
         * A description of what the function does, used by the model to choose when and how to call the function.
         */
        @SerialName("description") val description: String? = null,

        /**
         * Whether to enforce strict parameter validation. Default true.
         */
        @SerialName("strict") val strict: Boolean? = null,
    ) : ResponseTool

    /**
     * A tool that searches for relevant content from uploaded files.
     */
    @Serializable
    @SerialName("file_search")
    public data class FileSearch(
        /**
         * The IDs of the vector stores to search.
         */
        @SerialName("vector_store_ids")
        val vectorStoreIds: List<VectorStoreId> = emptyList(),
        /**
         * A filter to apply based on file attributes.
         */
        @SerialName("filters")
        val filters: FileSearchFilter? = null,

        /**
         * Ranking options for search.
         */
        @SerialName("ranking_options")
        val rankingOptions: FileSearchRankingOptions? = null,

        /**
         * The maximum number of results to return. This number should be between 1 and 50 inclusive.
         */
        @SerialName("max_num_results")
        val maxNumResults: Int? = null,
    ) : ResponseTool

    /**
     * Web search tool (preview)
     */
    @Serializable
    @SerialName("web_search_preview")
    public data class WebSearchPreview(
        /**
         * Approximate location parameters for the search.
         */
        @SerialName("user_location")
        val userLocation: WebSearchLocation? = null,

        /**
         * High level guidance for the amount of context window space to use for the search.
         * One of 'low', 'medium', or 'high'.
         * 'medium' is the default.
         */
        @SerialName("search_context_size")
        val searchContextSize: WebSearchContextSize? = null,
    ) : ResponseTool

    /**
     * Web search tool (preview 2025-03-11)
     */
    @Serializable
    @SerialName("web_search_preview_2025_03_11")
    public data class WebSearchPreview2025(

        /**
         * Approximate location parameters for the search.
         */
        @SerialName("user_location")
        val userLocation: WebSearchLocation? = null,

        /**
         * High level guidance for the amount of context window space to use for the search.
         * One of 'low', 'medium', or 'high'.
         * 'medium' is the default.
         */
        @SerialName("search_context_size")
        val searchContextSize: WebSearchContextSize? = null,
    ) : ResponseTool

    /**
     * Computer tool for computational tasks (preview)
     */
    @Serializable
    @SerialName("computer_use_preview")
    public data class ComputerUsePreview(

        /**
         * The width of the computer display
         */
        @SerialName("display_width")
        val displayWidth: Int,

        /**
         * The height of the computer display
         */
        @SerialName("display_height")
        val displayHeight: Int,

        /**
         * The type of computer environment to control
         */
        @SerialName("environment")
        val environment: String,
    ) : ResponseTool

    /**
     * Config for a remote MCP server.
     */
    @Serializable
    @SerialName("mcp")
    public data class MCP(
        /**
         * A label for this MCP server, used to identify it in tool calls.
         */
        @SerialName("server_label") val name: String,

        /**
         * The URL for the MCP server.
         */
        @SerialName("server_url") val serverUrl: String,

        /**
         * List of allowed tool names or a filter object.
         */
        @SerialName("allowed_tools") val allowedTools: MCPAllowedTools? = null,

        /**
         * Optional HTTP headers to send to the MCP server. Use for authentication or other purposes.
         */
        @SerialName("headers") val headers: Map<String, String>? = null,

        /**
         * Specify which of the MCP server's tools require approval.
         */
        @SerialName("require_approval") val requireApproval: MCPRequireApproval? = null,

        /**
         * Optional description of the MCP server, used to provide more context.
         */
        @SerialName("server_description") val serverDescription: String? = null,

        ) : ResponseTool

    /**
     * A tool that runs Python code to help generate a response to a prompt.
     */
    @Serializable
    @SerialName("code_interpreter")
    public data class CodeInterpreter(
        /**
         * The code interpreter container. Can be a container ID or an object that specifies uploaded file IDs to make available to your code.
         */
        @SerialName("container") val container: CodeInterpreterContainer,
    ) : ResponseTool

    /**
     * A tool that allows the model to execute shell commands in a local environment.
     */
    @Serializable
    @SerialName("local_shell")
    public class LocalShell : ResponseTool

    /**
     * A custom tool that processes input using a specified format.
     */
    @Serializable
    @SerialName("custom")
    public data class Custom(

        /** The name of the custom tool, used to identify it in tool calls. */
        @SerialName("name")
        val name: String,

        /** Optional description of the custom tool, used to provide more context. */
        @SerialName("description")
        val description: String? = null,

        /** The input format for the custom tool. Default is unconstrained text. */
        @SerialName("format")
        val format: CustomToolFormat? = null

    ) : ResponseTool
}
