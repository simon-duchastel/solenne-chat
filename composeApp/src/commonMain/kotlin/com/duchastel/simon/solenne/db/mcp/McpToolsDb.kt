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
     * Adds a new MCP server to the database.
     * Returns the successfully added server.
     */
    suspend fun addServer(server: McpServer): McpServer

    /**
     * Deletes an MCP server from the database.
     */
    suspend fun deleteServer(server: McpServer)
}