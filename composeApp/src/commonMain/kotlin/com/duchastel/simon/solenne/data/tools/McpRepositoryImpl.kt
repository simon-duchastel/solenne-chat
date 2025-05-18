package com.duchastel.simon.solenne.data.tools

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import com.duchastel.simon.solenne.data.tools.McpServer.Connection
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
import kotlinx.coroutines.flow.first
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
                        wrapMcpServerCall {
                            client.ping()
                        }.onFailure {
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
    ): McpServerStatus? {
        val server = McpServer(
            id = Uuid.random().toString(),
            name = name,
            connection = connection,
        )
        mcpServers += server

        return server.getCurrentStatus()
    }

    override suspend fun connect(server: McpServer): McpServerStatus? {
        // only SSE supported for now
        if (server.connection !is Connection.Sse) return server.getCurrentStatus()

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

        // ignore the status of the call
        wrapMcpServerCall {
            client.connect(sseTransport)
            clients += (server to client) // only add the client after a successful connection
            loadToolsForServer(server)
        }
        return server.getCurrentStatus()
    }

    override suspend fun disconnect(server: McpServer): McpServerStatus? {
        // no-op if already disconnected
        val client = clients[server] ?: return server.getCurrentStatus()

        // ignore the status of the call since we're disconnecting anyways - just don't crash
        wrapMcpServerCall {
            client.close()
        }
        clients -= server
        return server.getCurrentStatus()
    }

    override suspend fun loadToolsForServer(server: McpServer): List<Tool>? {
        val client = clients[server] ?: return emptyList()
        val toolsListResponse = wrapMcpServerCall {
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
        tools += (server to toolsParsed)
        return toolsParsed
    }

    override suspend fun callTool(
        server: McpServer,
        tool: Tool,
        arguments: Map<String, JsonElement?>,
    ): CallToolResult? {
        return wrapMcpServerCall {
            val client = clients[server] ?: error("Not connected to server")
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
        server: McpServer,
    ): Deferred<Unit> {
        val deferred = CompletableDeferred<Unit>()
        ioCoroutineScope.launch {
            try {
                // ignore any errors since loading new tools is
                // opportunistic
                val newTools = loadToolsForServer(server) ?: return@launch
                tools += (server to newTools)
            } finally {
                deferred.complete(Unit)
            }
        }
        return deferred
    }

    /**
     * Helper function that gets the status for a given [McpServer], or null
     * if it can't be found.
     */
    private suspend inline fun McpServer.getCurrentStatus(): McpServerStatus? {
        return serverStatusFlow().first().firstOrNull { it.mcpServer == this }
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

        /**
         * Map of MCP Server to MCP Client
         */
        private var clients by mutableStateOf<Map<McpServer, Client>>(emptyMap())

        /**
         * Map of MCP Server to MCP Client
         */
        private var mcpServers by mutableStateOf(listOf<McpServer>())

        /**
         * Map of MCP Server to MCP Tools
         */
        var tools by mutableStateOf<Map<McpServer, List<Tool>>>(emptyMap())
    }
}