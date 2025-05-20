package com.duchastel.simon.solenne.screens.topbar

import androidx.compose.runtime.Immutable
import com.duchastel.simon.solenne.parcel.Parcelize
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.screen.Screen

@Parcelize
data class TopBarScreen(
    val title: String,
) : Screen {
    @Immutable
    data class State(
        val title: String,
        val showBackButton: Boolean,
        val eventSink: (Event) -> Unit = {},
    ) : CircuitUiState

    sealed interface Event : CircuitUiEvent {
        data object BackPressed : Event
    }
}