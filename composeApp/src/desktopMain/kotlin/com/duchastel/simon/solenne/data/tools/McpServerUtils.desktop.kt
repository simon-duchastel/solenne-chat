package com.duchastel.simon.solenne.data.tools

import io.modelcontextprotocol.kotlin.sdk.server.StdioServerTransport
import kotlinx.io.InternalIoApi
import kotlinx.io.asSink
import kotlinx.io.asSource
import kotlinx.io.buffered
import kotlinx.io.bytestring.decodeToString
import kotlinx.io.readByteString

@OptIn(InternalIoApi::class)
actual fun createStdioServerTransport(connection: McpServerConfig.Connection.Stdio): StdioServerTransport? {
    val process = ProcessBuilder(
        connection.commandToRun.split(" ")
    ).start()
    return StdioServerTransport(
        inputStream = process.inputStream.asSource().buffered().also {
            val peekBuffer = it.peek().buffer.copy()
            println("Input Preview: " + peekBuffer.readByteString().decodeToString())
        },
        outputStream = process.outputStream.asSink().buffered().also {
            val peekBuffer = it.buffer.copy()
            println("Output Preview: " + peekBuffer.readByteString().decodeToString())
        },
    )
}