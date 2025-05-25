package com.duchastel.simon.solenne.data.tools

import io.modelcontextprotocol.kotlin.sdk.server.StdioServerTransport

actual fun createStdioServerTransport(connection: McpServerConfig.Connection.Stdio): StdioServerTransport? {
    return null
}