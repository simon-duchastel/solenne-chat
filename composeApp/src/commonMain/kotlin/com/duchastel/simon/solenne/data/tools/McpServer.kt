package com.duchastel.simon.solenne.data.tools

data class McpServer(
    val id: String,
    val name: String,
    val connection: Connection,
) {
    sealed interface  Connection {
        data class Stdio(
            internal val commandToRun: String,
        ): Connection

        data class Sse(
            internal val url: String,
        ): Connection
    }
}