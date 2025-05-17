package com.duchastel.simon.solenne.screens.splash

import androidx.compose.runtime.Immutable
import com.duchastel.simon.solenne.parcel.Parcelize
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.screen.Screen

@Parcelize
data object SplashScreen : Screen {
    @Immutable
    data class State(
        val eventSink: (Event) -> Unit = {},
    ) : CircuitUiState

    sealed interface Event : CircuitUiEvent
}