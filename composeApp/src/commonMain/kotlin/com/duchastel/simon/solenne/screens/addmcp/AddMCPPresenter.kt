package com.duchastel.simon.solenne.screens.addmcp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.duchastel.simon.solenne.data.tools.McpRepository
import com.duchastel.simon.solenne.data.tools.McpServer
import com.duchastel.simon.solenne.screens.addmcp.AddMCPScreen.Event
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
        var serverUrl by remember { mutableStateOf("") }

        val isSaveEnabled = serverName.isNotBlank() && serverUrl.isNotBlank()

        return AddMCPScreen.State(
            serverName = serverName,
            serverUrl = serverUrl,
            isSaveEnabled = isSaveEnabled,
        ) { event ->
            when (event) {
                is Event.BackPressed -> {
                    navigator.pop()
                }

                is Event.ServerNameChanged -> {
                    serverName = event.name
                }

                is Event.ServerUrlChanged -> {
                    serverUrl = event.url
                }

                is Event.SavePressed -> {
                    if (isSaveEnabled) {
                        coroutineScope.launch {
                            val connection = McpServer.Connection.Sse(url = serverUrl)
                            mcpRepository.addServer(serverName, connection)
                            navigator.pop()
                        }
                    }
                }
            }
        }
    }

    @AssistedFactory
    fun interface Factory {
        fun create(navigator: Navigator): AddMCPPresenter
    }
}