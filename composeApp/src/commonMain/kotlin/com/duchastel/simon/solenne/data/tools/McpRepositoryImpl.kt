package com.duchastel.simon.solenne.data.tools

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import com.duchastel.simon.solenne.data.tools.McpServer.Connection
import com.duchastel.simon.solenne.dispatchers.IODispatcher
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.ktor.client.HttpClient
import io.ktor.client.request.accept
import io.ktor.http.ContentType
import io.modelcontextprotocol.kotlin.sdk.Implementation
import io.modelcontextprotocol.kotlin.sdk.McpError
import io.modelcontextprotocol.kotlin.sdk.Method
import io.modelcontextprotocol.kotlin.sdk.TextContent
import io.modelcontextprotocol.kotlin.sdk.ToolListChangedNotification
import io.modelcontextprotocol.kotlin.sdk.client.Client
import io.modelcontextprotocol.kotlin.sdk.client.SseClientTransport
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.io.IOException
import kotlinx.serialization.json.JsonElement
import kotlin.time.Duration.Companion.seconds
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@SingleIn(AppScope::class)
@Inject
class McpRepositoryImpl(
    private val ioCoroutineScope: CoroutineScope = CoroutineScope(IODispatcher),
    private val httpClient: HttpClient,
): McpRepository {

    init {
        /**
         * Spawns an infinite loop that pings all clients to keep the
         * connection open. Whenever a client doesn't respond to a ping,
         * it's disconnected.
         */
        ioCoroutineScope.launch {
            while (true) {
                delay(HEARTBEAT_DELAY)
                clients.map { (server, client) ->
                    async {
                        try {
                            client.ping()
                        } catch (ex: IOException) {
                            disconnect(server)
                        } catch (ex: McpError) {
                            disconnect(server)
                        }
                    }
                }.awaitAll()
            }
        }
    }

    override fun serverStatusFlow(): Flow<List<McpServerStatus>> {
        return snapshotFlow {
            val mcpServersStatus = mcpServers.map {
                val status = when {
                    clients.contains(it) -> McpServerStatus.Status.Connected
                    else -> McpServerStatus.Status.Offline
                }
                val tools = tools[it] ?: emptyList()
                McpServerStatus(
                    mcpServer = it,
                    status = status,
                    tools = tools,
                )
            }
            mcpServersStatus
        }.distinctUntilChanged()
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun addServer(
        name: String,
        connection: Connection,
    ): McpServer {
        val server = McpServer(
            id = Uuid.random().toString(),
            name = name,
            connection = connection,
        )
        mcpServers += server

        return server
    }

    override suspend fun connect(server: McpServer) {
        if (server.connection !is Connection.Sse) return // only SSE supported for now

        val url = server.connection.url
        val sseTransport = SseClientTransport(
            client = httpClient,
            urlString = url,
            requestBuilder = {
                accept(ContentType.Application.Json)
                accept(ContentType.Text.EventStream)
            }
        ).apply {
            onClose { clients -= server }
        }

        val client = Client(clientInfo = clientInfo).apply {
            setNotificationHandler<ToolListChangedNotification>(
                method = Method.Defined.NotificationsToolsListChanged,
                handler = { handleToolChangedForServer(server) },
            )
        }
        clients += (server to client)

        client.connect(sseTransport)
        loadToolsForServer(server)
    }

    override suspend fun disconnect(server: McpServer) {
        val client = clients[server] ?: return // no-op if already disconnected
        client.close()
        clients -= server
    }

    override suspend fun loadToolsForServer(server: McpServer): List<Tool> {
        val client = clients[server] ?: return emptyList()
        val toolsResponse = client.listTools()
        val toolsParsed = toolsResponse?.tools?.map { toolRaw ->
            Tool(
                name = toolRaw.name,
                description = toolRaw.description,
                parameters = toolRaw.inputSchema.properties,
                requiredParameters = toolRaw.inputSchema.required ?: emptyList(),
            )
        } ?: emptyList()

        tools += (server to toolsParsed)
        return toolsParsed
    }

    override suspend fun callTool(
        server: McpServer,
        tool: Tool,
        arguments: Map<String, JsonElement?>,
    ): CallToolResult {
        val client = clients[server] ?: error("Not connected to server")
        val callToolResultRaw = client.callTool(tool.name, arguments)
        val text = (callToolResultRaw?.content?.get(0) as TextContent).text ?: error("No text returned")
        return CallToolResult(
            text = text,
            isError = callToolResultRaw.isError ?: false,
        )
    }

    /**
     * Helper function for setting a notification handler when the tool list
     * changes for a given server.
     */
    private fun handleToolChangedForServer(
        server: McpServer,
    ): Deferred<Unit> {
        val deferred = CompletableDeferred<Unit>()
        ioCoroutineScope.launch {
            val newTools = loadToolsForServer(server)
            tools += (server to newTools)
            deferred.complete(Unit)
        }
        return deferred
    }

    companion object {
        private val HEARTBEAT_DELAY = 2.seconds

        /**
         * Client information to communicate to the MCP servers.
         */
        private val clientInfo = Implementation(
            name = "Solenne",
            version = "0.1.0",
        )

        /**
         * Map of MCP Server to MCP Client
         */
        private var clients by mutableStateOf<Map<McpServer, Client>>(emptyMap())

        /**
         * Map of MCP Server to MCP Client
         */
        private var mcpServers by mutableStateOf<List<McpServer>>(emptyList())

        /**
         * Map of MCP Server to MCP Tools
         */
        var tools by mutableStateOf<Map<McpServer, List<Tool>>>(emptyMap())
    }
}