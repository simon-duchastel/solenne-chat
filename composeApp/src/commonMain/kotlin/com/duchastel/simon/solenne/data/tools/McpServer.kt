package com.duchastel.simon.solenne.data.tools

data class McpServer(
    val config: McpServerConfig,
    val status: Status,
    val tools: List<Tool>,
) {
    sealed interface Status {
        data object Connected : Status
        data object Offline : Status
    }
}