package com.duchastel.simon.solenne.db.mcp

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.duchastel.simon.solenne.Database
import com.duchastel.simon.solenne.data.tools.McpServer
import com.duchastel.simon.solenne.data.tools.Tool
import com.duchastel.simon.solenne.db.McpTool
import com.duchastel.simon.solenne.dispatchers.IODispatcher
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * SQLDelight implementation of [McpToolsDb].
 * Uses SQLDelight to persist MCP servers and tools.
 */
@Inject
class SqlDelightMcpToolsDb(
    private val database: Database,
    private val dispatcher: CoroutineDispatcher = IODispatcher,
) : McpToolsDb {

    @OptIn(ExperimentalTime::class)
    override fun getAllServers(): Flow<List<McpServer>> {
        return database.mcpServerQueries.getAllServers()
            .asFlow()
            .mapToList(dispatcher)
            .map { serverRows ->
                serverRows.map { row -> mcpServerRowToMcpServer(row) }
            }
    }

    override fun getServerById(id: String): Flow<McpServer?> {
        return database.mcpServerQueries.getServerById(id)
            .asFlow()
            .mapToOneOrNull(dispatcher)
            .map { serverRow ->
                serverRow?.let { row -> mcpServerRowToMcpServer(row) }
            }
    }

    @OptIn(ExperimentalTime::class)
    override suspend fun addServer(server: McpServer): McpServer {
        return withContext(dispatcher) {
            val connectionType: String
            val connectionUrl: String?
            val connectionCommand: String?
            when (val connection = server.connection) {
                is McpServer.Connection.Sse -> {
                    connectionType = "sse"
                    connectionUrl = connection.url
                    connectionCommand = null
                }

                is McpServer.Connection.Stdio -> {
                    connectionType = "stdio"
                    connectionUrl = null
                    connectionCommand = connection.commandToRun
                }
            }

            database.mcpServerQueries.insertServer(
                id = server.id,
                name = server.name,
                connection_type = connectionType,
                connection_url = connectionUrl,
                connection_command = connectionCommand,
                created_at = Clock.System.now().toEpochMilliseconds()
            )
            server
        }
    }

    override suspend fun deleteServer(serverId: String) {
        withContext(dispatcher) {
            database.mcpServerQueries.deleteToolsForServer(serverId)
            database.mcpServerQueries.deleteServer(serverId)
        }
    }

    override fun getToolsForServer(serverId: String): Flow<List<Tool>> {
        return database.mcpToolQueries.getToolsForServer(serverId)
            .asFlow()
            .mapToList(dispatcher)
            .map { toolRows ->
                toolRows.map { row -> mcpToolRowToTool(row) }
            }
    }

    override suspend fun updateToolsForServer(serverId: String, tools: List<Tool>): List<Tool> {
        return withContext(dispatcher) {
            // Delete all existing tools for the server
            database.mcpToolQueries.deleteToolsForServer(serverId)

            // Insert all new tools
            tools.forEach { tool ->
                val argumentsSchemaJson = Json.encodeToString(tool.argumentsSchema)
                val requiredArgumentsJson = Json.encodeToString(tool.requiredArguments)

                database.mcpToolQueries.insertTool(
                    server_id = serverId,
                    name = tool.name,
                    description = tool.description,
                    arguments_schema = argumentsSchemaJson,
                    required_arguments = requiredArgumentsJson
                )
            }

            tools
        }
    }

    /**
     * Converts a SQLDelight McpServer row to an McpServer object.
     */
    private fun mcpServerRowToMcpServer(row: com.duchastel.simon.solenne.db.McpServer): McpServer {
        val connection = when (row.connection_type) {
            "sse" -> McpServer.Connection.Sse(row.connection_url!!)
            "stdio" -> McpServer.Connection.Stdio(row.connection_command!!)
            else -> throw IllegalArgumentException("Unknown connection type: ${row.connection_type}")
        }

        return McpServer(
            id = row.id,
            name = row.name,
            connection = connection
        )
    }

    /**
     * Converts a SQLDelight McpTool row to a Tool object.
     */
    private fun mcpToolRowToTool(row: McpTool): Tool {
        val argumentsSchema = Json.decodeFromString<Map<String, JsonElement>>(row.arguments_schema)
        val requiredArguments = if (row.required_arguments != null) {
            Json.decodeFromString<List<String>>(row.required_arguments)
        } else {
            emptyList()
        }

        return Tool(
            name = row.name,
            description = row.description,
            argumentsSchema = argumentsSchema,
            requiredArguments = requiredArguments
        )
    }
}