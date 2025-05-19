package com.duchastel.simon.solenne.db.mcp

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.duchastel.simon.solenne.Database
import com.duchastel.simon.solenne.data.tools.McpServerConfig
import com.duchastel.simon.solenne.dispatchers.IODispatcher
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
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

    override fun getAllServers(): Flow<List<McpServerConfig>> {
        return database.mcpServerQueries.getAllServers()
            .asFlow()
            .mapToList(dispatcher)
            .map { serverRows ->
                serverRows.map { row -> mcpServerRowToMcpServer(row) }
            }
    }

    @OptIn(ExperimentalTime::class)
    override suspend fun addServer(server: McpServerConfig): McpServerConfig {
        return withContext(dispatcher) {
            val connectionType: String
            val connectionUrl: String?
            val connectionCommand: String?
            when (val connection = server.connection) {
                is McpServerConfig.Connection.Sse -> {
                    connectionType = "sse"
                    connectionUrl = connection.url
                    connectionCommand = null
                }

                is McpServerConfig.Connection.Stdio -> {
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

    override suspend fun deleteServer(server: McpServerConfig) {
        withContext(dispatcher) {
            database.mcpServerQueries.deleteServer(server.id)
        }
    }

    /**
     * Converts a SQLDelight McpServer row to an McpServer object.
     */
    private fun mcpServerRowToMcpServer(row: com.duchastel.simon.solenne.db.McpServer): McpServerConfig {
        val connection = when (row.connection_type) {
            "sse" -> McpServerConfig.Connection.Sse(row.connection_url!!)
            "stdio" -> McpServerConfig.Connection.Stdio(row.connection_command!!)
            else -> throw IllegalArgumentException("Unknown connection type: ${row.connection_type}")
        }

        return McpServerConfig(
            id = row.id,
            name = row.name,
            connection = connection
        )
    }
}