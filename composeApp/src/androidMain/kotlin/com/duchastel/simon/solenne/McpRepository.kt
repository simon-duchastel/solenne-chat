package com.duchastel.simon.solenne

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.modelcontextprotocol.kotlin.sdk.Implementation
import io.modelcontextprotocol.kotlin.sdk.client.Client
import io.modelcontextprotocol.kotlin.sdk.client.SseClientTransport
import io.ktor.client.HttpClient
import io.ktor.client.request.accept
import io.ktor.http.ContentType

@SingleIn(AppScope::class)
@Inject
class McpRepository(
    private val httpClient: HttpClient,
) {
    private val clients: MutableMap<String, Client> = mutableMapOf()

    /**
     * Connect to an MCP server at the given URL.
     */
    suspend fun connect(url: String) {
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
        clients[url] = client
    }

    /**
     * Disconnect from the MCP server at the given URL.
     */
    suspend fun disconnect(url: String) {
        val client = clients[url] ?: return // no-op if already disconnected
        println("TODO closing FOR $url")
        client.close()
        clients.remove(url)
    }

    /**
     * List tools available from the MCP server at the given URL.
     */
    suspend fun listTools(url: String): Any? {
        val client = clients[url] ?: return null
        return client.listTools()
    }

    /**
     * Call a specific tool on the MCP server at the given URL.
     */
    suspend fun callTool(
        url: String,
        toolId: String,
        arguments: Map<String, Any?> = emptyMap()
    ): Any? {
        val client = clients[url] ?: return null
        return client.callTool(toolId, arguments)
    }

    companion object {
        val clientInfo = Implementation(
            name = "Solenne",
            version = "0.1.0",
        )
    }
}