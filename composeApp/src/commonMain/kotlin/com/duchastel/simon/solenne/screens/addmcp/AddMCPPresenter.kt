package com.duchastel.simon.solenne.screens.addmcp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.duchastel.simon.solenne.data.tools.McpRepository
import com.duchastel.simon.solenne.data.tools.McpServerConfig
import com.duchastel.simon.solenne.screens.addmcp.AddMCPScreen.Event
import com.duchastel.simon.solenne.screens.addmcp.AddMCPScreen.ServerType
import com.duchastel.simon.solenne.screens.addmcp.AddMCPScreen.ServerConfig
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.launch

class AddMCPPresenter @Inject constructor(
    @Assisted private val navigator: Navigator,
    private val mcpRepository: McpRepository,
) : Presenter<AddMCPScreen.State> {

    @Composable
    override fun present(): AddMCPScreen.State {
        val coroutineScope = rememberCoroutineScope()
        var serverName by remember { mutableStateOf("") }
        var serverType by remember { mutableStateOf(ServerType.REMOTE) }
        var remoteConfig by remember { mutableStateOf(ServerConfig.Remote()) }
        var localConfig by remember { mutableStateOf(ServerConfig.Local()) }

        val saveEnabled = if (
            serverName.isNotEmpty() && when (serverType) {
                ServerType.REMOTE -> remoteConfig.isComplete
                ServerType.LOCAL -> localConfig.isComplete
            }
        ) {
            AddMCPScreen.SaveEnabled { name, config ->
                coroutineScope.launch {
                    val connection = when (config) {
                        is ServerConfig.Remote -> McpServerConfig.Connection.Sse(url = config.url)
                        is ServerConfig.Local -> McpServerConfig.Connection.Stdio(commandToRun = config.command)
                    }
                    mcpRepository.addServer(name, connection)
                    navigator.pop()
                }
            }
        } else {
            null
        }

        return AddMCPScreen.State(
            serverName = serverName,
            serverType = serverType,
            remoteConfig = remoteConfig,
            localConfig = localConfig,
            saveEnabled = saveEnabled
        ) { event ->
            when (event) {
                is Event.BackPressed -> {
                    navigator.pop()
                }
                is Event.ServerNameChanged -> {
                    serverName = event.name
                }
                is Event.ServerTypeChanged -> {
                    serverType = event.type
                }
                is Event.RemoteUrlChanged -> {
                    remoteConfig = remoteConfig.copy(url = event.url)
                }
                is Event.CommandChanged -> {
                    localConfig = localConfig.copy(command = event.command)
                }
                is Event.AddEnvironmentVariable -> {
                    localConfig = localConfig.copy(
                        environmentVariables = localConfig.environmentVariables + (event.name to event.value)
                    )
                }
                is Event.RemoveEnvironmentVariable -> {
                    localConfig = localConfig.copy(
                        environmentVariables = localConfig.environmentVariables.filterKeys { it != event.name }
                    )
                }
            }
        }
    }

    @AssistedFactory
    fun interface Factory {
        fun create(navigator: Navigator): AddMCPPresenter
    }
}
