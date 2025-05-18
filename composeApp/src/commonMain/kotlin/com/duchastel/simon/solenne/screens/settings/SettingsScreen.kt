package com.duchastel.simon.solenne.screens.settings

import androidx.compose.runtime.Immutable
import com.duchastel.simon.solenne.parcel.Parcelize
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.screen.Screen

@Parcelize
data object SettingsScreen : Screen {
    @Immutable
    data class State(
        val eventSink: (Event) -> Unit = {},
    ) : CircuitUiState

    sealed interface Event : CircuitUiEvent {
        data object BackPressed : Event
        data object ViewSourcePressed : Event
        data object ConfigureAIModelPressed : Event
        data object ConfigureMcpPressed : Event
    }
}