package com.duchastel.simon.solenne.fakes

import com.duchastel.simon.solenne.data.tools.CallToolResult
import com.duchastel.simon.solenne.data.tools.McpRepository
import com.duchastel.simon.solenne.data.tools.McpServer
import com.duchastel.simon.solenne.data.tools.McpServerStatus
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
    private val _statuses = MutableStateFlow<List<McpServerStatus>>(emptyList())
    override fun serverStatusFlow(): Flow<List<McpServerStatus>> = _statuses

    private var nextId = 1

    override suspend fun addServer(
        name: String,
        connection: McpServer.Connection
    ): McpServerStatus? {
        val server = McpServer(id = nextId.toString(), name = name, connection = connection)
        nextId++
        val serverStatus = McpServerStatus(
            mcpServer = server,
            status = McpServerStatus.Status.Offline,
            tools = emptyList()
        )
        _statuses.value += serverStatus
        return serverStatus
    }

    override suspend fun connect(server: McpServer): McpServerStatus? {
        var updatedStatus: McpServerStatus? = null

        _statuses.value = _statuses.value.map { status ->
            if (status.mcpServer == server) {
                val newStatus = status.copy(status = McpServerStatus.Status.Connected)
                updatedStatus = newStatus
                newStatus
            } else {
                status
            }
        }

        return updatedStatus
    }

    override suspend fun disconnect(server: McpServer): McpServerStatus? {
        var updatedStatus: McpServerStatus? = null

        _statuses.value = _statuses.value.map { status ->
            if (status.mcpServer == server) {
                val newStatus = status.copy(status = McpServerStatus.Status.Offline)
                updatedStatus = newStatus
                newStatus
            } else {
                status
            }
        }

        return updatedStatus
    }

    private val toolsMap = mutableMapOf<McpServer, List<Tool>>()

    override suspend fun loadToolsForServer(server: McpServer): List<Tool>? {
        return toolsMap[server]
    }

    override suspend fun callTool(
        server: McpServer,
        tool: Tool,
        arguments: Map<String, JsonElement?>
    ): CallToolResult? {
        val status = _statuses.value.find { it.mcpServer == server }
            ?: return null

        if (status.status != McpServerStatus.Status.Connected) {
            return null
        }

        return fakeCallToolResult
    }
}