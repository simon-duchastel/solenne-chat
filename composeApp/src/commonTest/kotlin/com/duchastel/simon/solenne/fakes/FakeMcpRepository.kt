package com.duchastel.simon.solenne.fakes

import com.duchastel.simon.solenne.data.tools.CallToolResult
import com.duchastel.simon.solenne.data.tools.McpRepository
import com.duchastel.simon.solenne.data.tools.McpServer
import com.duchastel.simon.solenne.data.tools.McpServerStatus
import com.duchastel.simon.solenne.data.tools.Tool
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

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

    override suspend fun addServer(name: String, connection: McpServer.Connection): McpServer {
        val server = McpServer(id = nextId.toString(), name = name, connection = connection)
        nextId++
        _statuses.value += McpServerStatus(
            mcpServer = server,
            status = McpServerStatus.Status.Offline,
            tools = emptyList()
        )
        return server
    }

    override suspend fun connect(server: McpServer) {
        _statuses.value = _statuses.value.map { status ->
            if (status.mcpServer == server) status.copy(status = McpServerStatus.Status.Connected) else status
        }
    }

    override suspend fun disconnect(server: McpServer) {
        _statuses.value = _statuses.value.map { status ->
            if (status.mcpServer == server) status.copy(status = McpServerStatus.Status.Offline) else status
        }
    }

    private val toolsMap = mutableMapOf<McpServer, List<Tool>>()

    override suspend fun loadToolsForServer(server: McpServer): List<Tool> {
        return toolsMap[server] ?: emptyList()
    }

    override suspend fun callTool(
        server: McpServer,
        toolId: String,
        arguments: Map<String, Any?>
    ): CallToolResult {
        val status = _statuses.value.find { it.mcpServer == server }
            ?: throw IllegalStateException("Server not found: \\$server")
        if (status.status != McpServerStatus.Status.Connected) {
            throw IllegalStateException("Server not connected: \\$server")
        }
        return fakeCallToolResult
    }
}
