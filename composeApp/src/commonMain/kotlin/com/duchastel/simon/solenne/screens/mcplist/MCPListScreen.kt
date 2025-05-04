package com.duchastel.simon.solenne.screens.mcplist

import androidx.compose.runtime.Immutable
import com.duchastel.simon.solenne.parcel.Parcelize
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.screen.Screen
import kotlinx.collections.immutable.PersistentList

@Parcelize
data object MCPListScreen : Screen {
    @Immutable
    data class State(
        val mcpServers: PersistentList<UIMCPServer>,
        val eventSink: (Event) -> Unit = {},
    ) : CircuitUiState

    sealed interface Event : CircuitUiEvent {
        data object BackPressed : Event
        data class ConnectToServer(val server: UIMCPServer) : Event
    }
}

data class UIMCPServer(
    val id: String,
    val name: String,
    val status: Status
) {
    sealed interface Status {
        data object Connected : Status
        data object Disconnected : Status
    }
}