package com.duchastel.simon.solenne.data.tools

import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.JsonElement

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
    fun serverStatusFlow(): Flow<List<McpServerStatus>>

    /**
     * Add a new MCP server configuration for a server.
     * This will not connect to the server - that must be
     * done separately through [connect].
     */
    suspend fun addServer(
        name: String,
        connection: McpServer.Connection,
    ): McpServer

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
    suspend fun loadToolsForServer(server: McpServer): List<Tool>

    /**
     * Call a specific tool on the MCP server [server].
     */
    suspend fun callTool(
        server: McpServer,
        tool: Tool,
        arguments: Map<String, JsonElement?>,
    ): CallToolResult
}