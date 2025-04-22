package com.duchastel.simon.solenne.data.tools

import com.duchastel.simon.solenne.data.tools.McpServer.Connection
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.ktor.client.HttpClient
import io.ktor.client.request.accept
import io.ktor.http.ContentType
import io.modelcontextprotocol.kotlin.sdk.Implementation
import io.modelcontextprotocol.kotlin.sdk.client.Client
import io.modelcontextprotocol.kotlin.sdk.client.SseClientTransport
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@SingleIn(AppScope::class)
@Inject
class McpRepositoryImpl(
    private val httpClient: HttpClient,
): McpRepository {

    // TODO - implement
    override suspend fun availableServers(): Flow<List<McpServer>> {
        return flowOf()
    }

    // TODO - implement
    override suspend fun addServer(server: McpServer) = Unit

    override suspend fun connect(server: McpServer) {
        if (server.connection !is Connection.Sse) return // only SSE supported for now

        val url = server.connection.url
        val client = Client(clientInfo = clientInfo).apply {
            connect(
                SseClientTransport(
                    client = httpClient,
                    urlString = url,
                    requestBuilder = {
                        accept(ContentType.Application.Json)
                        accept(ContentType.Text.EventStream)
                    }
                )
            )
        }
        clients[server.id] = client
    }

    override suspend fun disconnect(server: McpServer) {
        val client = clients[server.id] ?: return // no-op if already disconnected
        client.close()
        clients.remove(server.id)
    }

    override suspend fun listTools(server: McpServer): Any? {
        val client = clients[server.id] ?: return null
        return client.listTools()
    }

    override suspend fun callTool(
        server: McpServer,
        toolId: String,
        arguments: Map<String, Any?>,
    ): Any? {
        val client = clients[server.id] ?: return null
        return client.callTool(toolId, arguments)
    }

    companion object {
        /**
         * Map of MCP Server ID to MCP Client
         */
        private val clients: MutableMap<String, Client> = mutableMapOf()

        /**
         * Client information to communicate to the MCP servers.
         */
        private val clientInfo = Implementation(
            name = "Solenne",
            version = "0.1.0",
        )
    }
}