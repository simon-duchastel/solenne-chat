package com.duchastel.simon.solenne.fakes

import com.duchastel.simon.solenne.data.tools.McpServerConfig
import com.duchastel.simon.solenne.db.mcp.McpToolsDb
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * A fake implementation of McpToolsDb for testing.
 */
class FakeMcpToolsDb : McpToolsDb {
    private val servers = MutableStateFlow<List<McpServerConfig>>(emptyList())

    override fun getAllServers(): Flow<List<McpServerConfig>> = servers

    override suspend fun addServer(server: McpServerConfig): McpServerConfig {
        servers.value += server
        return server
    }

    override suspend fun deleteServer(server: McpServerConfig) {
        servers.value = servers.value.filter { it.id != server.id }
    }
}
