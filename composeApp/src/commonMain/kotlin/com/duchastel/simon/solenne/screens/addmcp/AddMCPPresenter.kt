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
import com.duchastel.simon.solenne.screens.addmcp.AddMCPScreen.ServerConfig
import com.duchastel.simon.solenne.screens.addmcp.AddMCPScreen.ServerType
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
        var serverName: String? by remember { mutableStateOf(null) }
        var serverType by remember { mutableStateOf(ServerType.REMOTE) }
        var remoteConfigUrl: String? by remember { mutableStateOf(null) }
        var localConfigCommand: String? by remember { mutableStateOf("") }
        var localConfigEnvironmentVariables: Map<String, String> by remember {
            mutableStateOf(emptyMap())
        }

        val saveEnabled = if (
            !serverName.isNullOrEmpty() && when (serverType) {
                ServerType.REMOTE -> !remoteConfigUrl.isNullOrEmpty()
                ServerType.LOCAL -> !localConfigCommand.isNullOrEmpty()
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

        val serverConfig = when (serverType) {
            ServerType.REMOTE -> {
                ServerConfig.Remote(
                    url = remoteConfigUrl ?: "",
                    onUrlChanged = { remoteConfigUrl = it }
                )
            }
            ServerType.LOCAL -> {
                ServerConfig.Local(
                    command = localConfigCommand ?: "",
                    environmentVariables = localConfigEnvironmentVariables,
                    onCommandChanged = { localConfigCommand = it },
                    onEnvironmentVariableUpdated = { key, value ->
                        if (value == null) {
                            localConfigEnvironmentVariables -= key
                        } else {
                            localConfigEnvironmentVariables += (key to value)
                        }
                    }
                )
            }
        }

        return AddMCPScreen.State(
            serverName = serverName ?: "",
            saveEnabled = saveEnabled,
            config = serverConfig,
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
            }
        }
    }

    @AssistedFactory
    fun interface Factory {
        fun create(navigator: Navigator): AddMCPPresenter
    }
}
