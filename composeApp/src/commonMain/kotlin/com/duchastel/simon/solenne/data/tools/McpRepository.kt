package com.duchastel.simon.solenne.data.tools

import kotlinx.coroutines.flow.Flow

/**
 * Repository for interacting with MCP servers.
 *
 * Responsible for managing connections and using tools on MCP servers.
 */
interface McpRepository {

    /**
     * List the currently available MCP servers. Note that this includes
     * all servers the user has added including those which are currently
     * disconnected, not just those with active connections.
     */
    suspend fun availableServers(): Flow<List<McpServer>>

    /**
     * Add a new MCP server configuration. This will not connect to the server -
     * that must be done separately through [connect].
     */
    suspend fun addServer(server: McpServer)

    /**
     * Connect to an MCP server at the given URL.
     */
    suspend fun connect(server: McpServer)

    /**
     * Disconnect from the MCP server at the given URL.
     */
    suspend fun disconnect(server: McpServer)

    /**
     * List tools available from the MCP server at the given URL.
     */
    suspend fun listTools(server: McpServer): Any?

    /**
     * Call a specific tool on the MCP server at the given URL.
     */
    suspend fun callTool(
        server: McpServer,
        toolId: String,
        arguments: Map<String, Any?> = emptyMap()
    ): Any?
}