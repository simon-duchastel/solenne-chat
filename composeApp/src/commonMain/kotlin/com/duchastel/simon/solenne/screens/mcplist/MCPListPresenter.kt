package com.duchastel.simon.solenne.screens.mcplist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import com.duchastel.simon.solenne.data.tools.McpRepository
import com.duchastel.simon.solenne.data.tools.McpServerStatus
import com.duchastel.simon.solenne.screens.mcplist.MCPListScreen.Event
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.launch

@SingleIn(AppScope::class)
class MCPListPresenter @Inject constructor(
    @Assisted private val navigator: Navigator,
    private val mcpRepository: McpRepository,
) : Presenter<MCPListScreen.State> {

    @Composable
    override fun present(): MCPListScreen.State {
        val coroutineScope = rememberCoroutineScope()

        val servers by mcpRepository.serverStatusFlow().collectAsState(initial = emptyList())

        return MCPListScreen.State(
            mcpServers = servers.map(McpServerStatus::toUiModel).toPersistentList(),
        ) { event ->
            when (event) {
                is Event.BackPressed -> {
                    navigator.pop()
                }
                is Event.ConnectToServer -> coroutineScope.launch {
                    // TODO - handle and log the unexpected null case
                    val server = servers.find { it.mcpServer.id == event.server.id } ?: return@launch
                    mcpRepository.connect(server.mcpServer)
                }
            }
        }
    }

    @AssistedFactory
    fun interface Factory {
        fun create(navigator: Navigator): MCPListPresenter
    }
}

fun McpServerStatus.toUiModel(): UIMCPServer {
    return UIMCPServer(
        id = mcpServer.id,
        name = mcpServer.name,
        status = when (status) {
            is McpServerStatus.Status.Connected -> UIMCPServer.Status.Connected
            is McpServerStatus.Status.Offline -> UIMCPServer.Status.Disconnected
        },
    )
}