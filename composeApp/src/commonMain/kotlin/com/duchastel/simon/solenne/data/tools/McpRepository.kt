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
     *
     * Returns [McpServerStatus] of the server was added successfully,
     * null otherwise.
     */
    suspend fun addServer(
        name: String,
        connection: McpServer.Connection,
    ): McpServerStatus?

    /**
     * Connect to the MCP server [server].
     *
     * Returns [McpServerStatus] if the server was connected successfully,
     * null otherwise.
     */
    suspend fun connect(server: McpServer): McpServerStatus?

    /**
     * Disconnect from the MCP server [server].
     */
    suspend fun disconnect(server: McpServer): McpServerStatus?

    /**
     * Fetches an updated list of tools available from the MCP server [server].
     *
     * Note that the tools should already be synced via a notification listener,
     * so prefer [serverStatusFlow] unless you need to force a refresh.
     *
     * Returns the list of tools, or null if the tools could not be loaded.
     */
    suspend fun loadToolsForServer(server: McpServer): List<Tool>?

    /**
     * Call a specific tool on the MCP server [server].
     *
     * Returns the result of the tool call, or null if the call
     * could not be successfully made.
     *
     * Note that null does not necessarily mean the result of the
     * call on the MCP server's side was a failure - only that the
     * connection to the MCP server could not properly be completed.
     */
    suspend fun callTool(
        server: McpServer,
        tool: Tool,
        arguments: Map<String, JsonElement?>,
    ): CallToolResult?
}