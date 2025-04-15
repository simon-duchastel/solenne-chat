package org.duchastel.simon.solenne.screens.chat

import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.screen.Screen
import org.duchastel.simon.solenne.parcel.Parcelize

@Parcelize
data object ChatScreen: Screen {
    data class State(
        val eventSink: (Event) -> Unit = {},
    ): CircuitUiState

    sealed interface Event : CircuitUiEvent
}