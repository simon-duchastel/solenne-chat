package com.duchastel.simon.solenne.data.tools

import com.duchastel.simon.solenne.data.tools.McpServerConfig.Connection
import io.modelcontextprotocol.kotlin.sdk.server.StdioServerTransport

expect fun createStdioServerTransport(connection: Connection.Stdio): StdioServerTransport?