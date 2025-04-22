package com.duchastel.simon.solenne.data.tools

data class McpServer(
    val id: String,
    val status: Status,
    internal val connection: Connection
) {
    sealed interface Status {
        data object Connected : Status
        data object Offline : Status
    }

    sealed interface  Connection {
        data class Stdio internal constructor(
            internal val commandToRun: String,
        ): Connection

        data class Sse internal constructor(
            internal val url: String,
        ): Connection
    }
}