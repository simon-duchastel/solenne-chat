package com.duchastel.simon.solenne.data.tools

data class McpServer internal constructor(
    val id: String,
    val name: String,
    val connection: Connection,
) {
    sealed interface  Connection {
        data class Stdio internal constructor(
            internal val commandToRun: String,
        ): Connection

        data class Sse internal constructor(
            internal val url: String,
        ): Connection
    }
}