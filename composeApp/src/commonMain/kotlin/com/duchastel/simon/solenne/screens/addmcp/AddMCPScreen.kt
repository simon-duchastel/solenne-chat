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
        val serverName: String = "",
        val serverUrl: String = "",
        val isSaveEnabled: Boolean = false,
        val eventSink: (Event) -> Unit = {},
    ) : CircuitUiState

    sealed interface Event : CircuitUiEvent {
        data object BackPressed : Event
        data class ServerNameChanged(val name: String) : Event
        data class ServerUrlChanged(val url: String) : Event
        data object SavePressed : Event
    }
}