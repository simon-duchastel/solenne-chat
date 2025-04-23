package com.duchastel.simon.solenne.data.tools

import kotlinx.coroutines.flow.Flow

/**
 * Repository for interacting with MCP servers.
 *
 * Responsible for managing connections and using tools on MCP servers.
 */
interface McpRepository {

    /**
     * List the currently available MCP servers and their statuses.
     * Note that this includes all servers the user has added including
     * those which are currently disconnected, not just those with
     * active connections.
     */
    suspend fun serverStatusFlow(): Flow<List<McpServerStatus>>

    /**
     * Add a new MCP server configuration for [server].
     * This will not connect to the server - that must be
     * done separately through [connect].
     */
    suspend fun addServer(server: McpServer)

    /**
     * Connect to the MCP server [server].
     */
    suspend fun connect(server: McpServer)

    /**
     * Disconnect from the MCP server [server].
     */
    suspend fun disconnect(server: McpServer)

    /**
     * List tools available from the MCP server [server].
     */
    suspend fun listTools(server: McpServer): List<Tool>

    /**
     * Call a specific tool on the MCP server [server].
     */
    suspend fun callTool(
        server: McpServer,
        toolId: String,
        arguments: Map<String, Any?> = emptyMap()
    ): CallToolResult
}