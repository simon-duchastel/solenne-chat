package com.duchastel.simon.solenne.data.tools

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import com.duchastel.simon.solenne.data.tools.McpServerConfig.Connection
import com.duchastel.simon.solenne.db.mcp.McpToolsDb
import com.duchastel.simon.solenne.dispatchers.IODispatcher
import com.duchastel.simon.solenne.util.types.Failure
import com.duchastel.simon.solenne.util.types.SolenneResult
import com.duchastel.simon.solenne.util.types.Success
import com.duchastel.simon.solenne.util.types.onFailure
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.ktor.client.HttpClient
import io.ktor.client.plugins.sse.SSEClientException
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
import kotlinx.coroutines.flow.onEach
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
    private val mcpToolsDb: McpToolsDb,
): McpRepository {

    init {
        ioCoroutineScope.launch {
            while (true) {
                delay(HEARTBEAT_DELAY)
                mcpServerStatuses.map { (id, status) ->
                    async {
                        wrapMcpServerCall {
                            status.client?.ping()
                        }.onFailure {
                            disconnect(id)
                        }
                    }
                }.awaitAll()
            }
        }

        ioCoroutineScope.launch {
            mcpToolsDb.getAllServers()
                .distinctUntilChanged()
                .collect { serversFromDb ->
                    serversFromDb.forEach { serverConfigFromDb ->
                        val existingConnection = mcpServerStatuses[serverConfigFromDb.id]
                        mcpServerStatuses += (serverConfigFromDb.id to
                            McpServerStatus(
                                config = serverConfigFromDb,
                                client = existingConnection?.client,
                                tools = existingConnection?.tools ?: emptyList(),
                            )
                        )

                        // if this is our first time adding this server,
                        // try to connect to it
                        if (existingConnection == null) {
                            connect(serverConfigFromDb)
                        }
                    }
                }
        }
    }

    override fun serverStatusFlow(): Flow<List<McpServer>> {
        return snapshotFlow {
            mcpServerStatuses.values.map { it.toMcpServer() }
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun addServer(
        name: String,
        connection: Connection,
    ): McpServerConfig {
        val server = McpServerConfig(
            id = Uuid.random().toString(),
            name = name,
            connection = connection,
        )
        mcpToolsDb.addServer(server)

        return server
    }

    override suspend fun connect(server: McpServerConfig): McpServer? {
        // only SSE supported for now
        if (server.connection !is Connection.Sse) return mcpServerStatuses[server.id]?.toMcpServer()

        // if we already have an existing connection, don't try to reconnect
        val existingConnection = mcpServerStatuses[server.id]
        if (existingConnection?.client != null) return existingConnection.toMcpServer()

        val url = server.connection.url
        val sseTransport = SseClientTransport(
            client = httpClient,
            urlString = url,
            requestBuilder = {
                accept(ContentType.Application.Json)
                accept(ContentType.Text.EventStream)
            }
        ).apply {
            onClose {
                val connectionToClose = mcpServerStatuses[server.id] ?: return@onClose
                mcpServerStatuses += (server.id to connectionToClose.copy(client = null))
            }
        }

        val client = Client(clientInfo = clientInfo).apply {
            setNotificationHandler<ToolListChangedNotification>(
                method = Method.Defined.NotificationsToolsListChanged,
                handler = { handleToolChangedForServer(server) },
            )
        }

        val serverStatus = wrapMcpServerCall {
            client.connect(sseTransport)
            val serverStatus = McpServerStatus(
                config = server,
                client = client,
                tools = existingConnection?.tools ?: emptyList(),
            )
            mcpServerStatuses += (server.id to serverStatus)
            loadToolsForServer(server)
            serverStatus
        }
        return serverStatus.getOrNull()?.toMcpServer()
    }

    override suspend fun disconnect(serverId: String): String {
        val server = mcpServerStatuses[serverId] ?: return serverId

        wrapMcpServerCall {
            server.client?.close()
        }
        mcpServerStatuses += (serverId to server.copy(client = null))
        return serverId
    }

    override suspend fun loadToolsForServer(server: McpServerConfig): List<Tool>? {
        val serverInstance = mcpServerStatuses[server.id] ?: return null
        val client = serverInstance.client ?: return null
        val toolsListResponse = wrapMcpServerCall {
            // server encodes "no tools" as null, while we encode it as empty list
            client.listTools()?.tools ?: emptyList()
        }

        if (toolsListResponse !is Success) return null
        val toolsParsed = toolsListResponse().map { toolRaw ->
            Tool(
                name = toolRaw.name,
                description = toolRaw.description,
                argumentsSchema = toolRaw.inputSchema.properties,
                requiredArguments = toolRaw.inputSchema.required ?: emptyList(),
            )
        }
        mcpServerStatuses += server.id to serverInstance.copy(tools = toolsParsed)

        return toolsParsed
    }

    override suspend fun callTool(
        server: McpServerConfig,
        tool: Tool,
        arguments: Map<String, JsonElement?>,
    ): CallToolResult? {
        return wrapMcpServerCall {
            val client = mcpServerStatuses[server.id]?.client ?: error("Not connected to server")
            val callToolResultRaw = client.callTool(tool.name, arguments)
            val text = (callToolResultRaw?.content?.get(0) as TextContent).text
                ?: error("No text returned")

            CallToolResult(
                text = text,
                isError = callToolResultRaw.isError ?: false,
            )
        }.getOrNull()
    }

    /**
     * Helper function for setting a notification handler when the tool list
     * changes for a given server.
     */
    private fun handleToolChangedForServer(
        server: McpServerConfig,
    ): Deferred<Unit> {
        val deferred = CompletableDeferred<Unit>()
        ioCoroutineScope.launch {
            try {
                loadToolsForServer(server)
            } finally {
                deferred.complete(Unit)
            }
        }
        return deferred
    }

    /**
     * Convenience function to wrap a call made to the MCP library.
     * Automatically catches and wraps any exceptions thrown by the MCP library such
     * as [IOException] and [McpError].
     */
    private suspend fun <T> wrapMcpServerCall(block: suspend () -> T): SolenneResult<T> {
        return try {
            val successResult = block()
            Success(successResult)
        } catch (ex: IOException) {
            // thrown by OkHttp (via the MCP library) when an error occurs
            // communicating over the network
            Failure(ex)
        } catch (ex: SSEClientException) {
            // thrown by OkHttp (via the MCP library) when an error occurs
            // in the SSE HTTP stream
            Failure(ex)
        } catch (ex: IllegalStateException) {
            // thrown by the MCP library when the client is not connected
            Failure(ex)
        } catch (ex: McpError) {
            // thrown by the MCP library for certain protocol errors
            Failure(ex)
        }
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

        private var mcpServerStatuses by mutableStateOf(mapOf<String, McpServerStatus>())
    }

    data class McpServerStatus(
        val config: McpServerConfig,
        val client: Client?,
        val tools: List<Tool>,
    )

    private fun McpServerStatus.toMcpServer(): McpServer {
        return McpServer(
            config = this.config,
            tools = this.tools,
            status = if (this.client != null) {
                McpServer.Status.Connected
            } else {
                McpServer.Status.Offline
            },
        )
    }
}