package com.duchastel.simon.solenne.db.mcp

import com.duchastel.simon.solenne.data.tools.McpServer
import com.duchastel.simon.solenne.data.tools.Tool
import kotlinx.coroutines.flow.Flow

/**
 * Interface for the MCP tools database.
 * Responsible for persisting MCP servers and tools.
 */
interface McpToolsDb {
    /**
     * Gets all MCP servers. Returns the list of MCP server objects.
     */
    fun getAllServers(): Flow<List<McpServer>>

    /**
     * Gets a specific MCP server by its ID.
     * Returns the server if found, or null otherwise.
     */
    fun getServerById(id: String): Flow<McpServer?>

    /**
     * Adds a new MCP server to the database.
     * Returns the successfully added server.
     */
    suspend fun addServer(server: McpServer): McpServer

    /**
     * Deletes an MCP server from the database.
     */
    suspend fun deleteServer(serverId: String)

    /**
     * Gets all tools for a specific MCP server.
     * Returns the list of tools for the server.
     */
    fun getToolsForServer(serverId: String): Flow<List<Tool>>

    /**
     * Updates the tools for a specific MCP server.
     * This will replace all existing tools for the server with the new list.
     * Returns the updated list of tools.
     */
    suspend fun updateToolsForServer(serverId: String, tools: List<Tool>): List<Tool>
}