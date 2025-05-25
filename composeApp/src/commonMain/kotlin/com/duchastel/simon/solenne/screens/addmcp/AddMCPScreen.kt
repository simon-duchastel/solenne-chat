package com.duchastel.simon.solenne.screens.addmcp

import androidx.compose.runtime.Immutable
import com.duchastel.simon.solenne.parcel.Parcelize
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.screen.Screen

@Parcelize
data object AddMCPScreen : Screen {
    @Immutable
    data class State(
        val serverName: String,
        val config: ServerConfig,
        val saveEnabled: SaveEnabled?,
        val eventSink: (Event) -> Unit = {},
    ) : CircuitUiState

    data class SaveEnabled(
        val onSavePressed: (serverName: String, config: ServerConfig) -> Unit
    )

    enum class ServerType {
        REMOTE, LOCAL
    }

    sealed interface ServerConfig {
        data class Remote(
            val url: String,
            val onUrlChanged: (String) -> Unit,
        ) : ServerConfig

        data class Local(
            val command: String,
            val environmentVariables: Map<String, String>,
            val onCommandChanged: (String) -> Unit,
            val onEnvironmentVariableUpdated: (String, String?) -> Unit,
        ) : ServerConfig
    }

    sealed interface Event : CircuitUiEvent {
        data object BackPressed : Event
        data class ServerNameChanged(val name: String) : Event
        data class ServerTypeChanged(val type: ServerType) : Event
    }
}
