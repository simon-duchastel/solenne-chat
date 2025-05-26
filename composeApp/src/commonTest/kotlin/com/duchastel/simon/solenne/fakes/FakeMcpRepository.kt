package com.duchastel.simon.solenne.fakes

import com.duchastel.simon.solenne.data.tools.CallToolResult
import com.duchastel.simon.solenne.data.tools.McpRepository
import com.duchastel.simon.solenne.data.tools.McpServer
import com.duchastel.simon.solenne.data.tools.McpServerConfig
import com.duchastel.simon.solenne.data.tools.Tool
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.json.JsonElement

/**
 * A fake McpRepository for testing that tracks servers, their connection state,
 * available tools, and returns a configurable fake call result.
 */
internal class FakeMcpRepository(
    private val fakeCallToolResult: CallToolResult = CallToolResult(
        text = "fake-result",
        isError = false
    )
) : McpRepository {
    private val _statuses = MutableStateFlow<List<McpServer>>(emptyList())
    override fun serverStatusFlow(): Flow<List<McpServer>> = _statuses

    private var nextId = 1

    // Helper method for tests to get current servers
    fun getServers(): List<McpServer> = _statuses.value

    override suspend fun addServer(
        name: String,
        connection: McpServerConfig.Connection
    ): McpServerConfig {
        val server = McpServerConfig(id = nextId.toString(), name = name, connection = connection)
        nextId++
        val serverStatus = McpServer(
            config = server,
            status = McpServer.Status.Offline,
            tools = emptyList()
        )
        _statuses.value += serverStatus
        return server
    }

    override suspend fun connect(server: McpServerConfig): McpServer? {
        var updatedStatus: McpServer? = null

        _statuses.value = _statuses.value.map { status ->
            if (status.config == server) {
                val newStatus = status.copy(status = McpServer.Status.Connected)
                updatedStatus = newStatus
                newStatus
            } else {
                status
            }
        }

        return updatedStatus
    }

    override suspend fun disconnect(serverId: String): String? {
        var updatedServerId: String? = null

        _statuses.value = _statuses.value.map { status ->
            if (status.config.id == serverId) {
                updatedServerId = serverId
                status.copy(status = McpServer.Status.Offline)
            } else {
                status
            }
        }

        return updatedServerId
    }

    private val toolsMap = mutableMapOf<McpServerConfig, List<Tool>>()

    override suspend fun loadToolsForServer(server: McpServerConfig): List<Tool>? {
        return toolsMap[server]
    }

    override suspend fun callTool(
        server: McpServerConfig,
        tool: Tool,
        arguments: Map<String, JsonElement?>
    ): CallToolResult? {
        val status = _statuses.value.find { it.config == server } ?: return null

        if (status.status != McpServer.Status.Connected) {
            return null
        }

        return fakeCallToolResult
    }
}
