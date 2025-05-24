package com.duchastel.simon.solenne.screens.addmcp

import androidx.compose.runtime.Immutable
import com.duchastel.simon.solenne.parcel.Parcelize
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.screen.Screen

@Parcelize
data object AddMCPScreen : Screen {
    enum class ServerType {
        REMOTE, LOCAL
    }
    
    sealed interface ServerConfig {
        val isComplete: Boolean
        
        data class Remote(
            val url: String = ""
        ) : ServerConfig {
            override val isComplete: Boolean
                get() = url.isNotEmpty()
        }
        
        data class Local(
            val command: String = "",
            val environmentVariables: Map<String, String> = emptyMap()
        ) : ServerConfig {
            override val isComplete: Boolean
                get() = command.isNotEmpty()
        }
    }

    @Immutable
    data class State(
        val serverName: String,
        val serverType: ServerType = ServerType.REMOTE,
        val remoteConfig: ServerConfig.Remote = ServerConfig.Remote(),
        val localConfig: ServerConfig.Local = ServerConfig.Local(),
        val saveEnabled: SaveEnabled?,
        val eventSink: (Event) -> Unit = {},
    ) : CircuitUiState {
        val isSaveEnabled: Boolean
            get() = serverName.isNotEmpty() && when (serverType) {
                ServerType.REMOTE -> remoteConfig.isComplete
                ServerType.LOCAL -> localConfig.isComplete
            }
    }

    data class SaveEnabled(
        val onSavePressed: (serverName: String, config: ServerConfig) -> Unit
    )

    sealed interface Event : CircuitUiEvent {
        data object BackPressed : Event
        data class ServerNameChanged(val name: String) : Event
        data class ServerTypeChanged(val type: ServerType) : Event
        data class RemoteUrlChanged(val url: String) : Event
        data class CommandChanged(val command: String) : Event
        data class AddEnvironmentVariable(val name: String, val value: String) : Event
        data class RemoveEnvironmentVariable(val name: String) : Event
    }
}
