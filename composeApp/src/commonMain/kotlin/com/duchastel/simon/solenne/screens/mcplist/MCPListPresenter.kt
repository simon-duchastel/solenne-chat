package com.duchastel.simon.solenne.screens.mcplist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import com.duchastel.simon.solenne.data.tools.McpRepository
import com.duchastel.simon.solenne.data.tools.McpServer
import com.duchastel.simon.solenne.screens.addmcp.AddMCPScreen
import com.duchastel.simon.solenne.screens.mcplist.MCPListScreen.Event
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.Inject
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.launch

class MCPListPresenter @Inject constructor(
    @Assisted private val navigator: Navigator,
    private val mcpRepository: McpRepository,
) : Presenter<MCPListScreen.State> {

    @Composable
    override fun present(): MCPListScreen.State {
        val coroutineScope = rememberCoroutineScope()

        val servers by mcpRepository.serverStatusFlow().collectAsState(initial = emptyList())

        return MCPListScreen.State(
            mcpServers = servers.map(McpServer::toUiModel).toPersistentList(),
        ) { event ->
            when (event) {
                is Event.BackPressed -> {
                    navigator.pop()
                }
                is Event.ConnectToServer -> coroutineScope.launch {
                    // TODO - handle and log the unexpected null case
                    val server = servers.find { it.config.id == event.server.id } ?: return@launch
                    mcpRepository.connect(server.config)
                }
                is Event.AddServerPressed -> {
                    navigator.goTo(AddMCPScreen)
                }
            }
        }
    }

    @AssistedFactory
    fun interface Factory {
        fun create(navigator: Navigator): MCPListPresenter
    }
}

fun McpServer.toUiModel(): UIMCPServer {
    return UIMCPServer(
        id = config.id,
        name = config.name,
        status = when (status) {
            is McpServer.Status.Connected -> UIMCPServer.Status.Connected
            is McpServer.Status.Offline -> UIMCPServer.Status.Disconnected
        },
    )
}