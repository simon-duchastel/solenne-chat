package com.duchastel.simon.solenne.screens.modelproviderconfig

import androidx.compose.runtime.Immutable
import com.duchastel.simon.solenne.data.ai.AIModelProvider
import com.duchastel.simon.solenne.parcel.Parcelize
import com.duchastel.simon.solenne.screens.modelproviderselector.UiModelProvider
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.screen.Screen

@Parcelize
data class ModelProviderConfigScreen(
    val modelProvider: AIModelProvider,
) : Screen {
    @Immutable
    data class State(
        val modelProvider: UiModelProvider,
        val apiKey: String? = null,
        val eventSink: (Event) -> Unit = {},
    ) : CircuitUiState

    sealed interface Event : CircuitUiEvent {
        data class ApiKeyChanged(val apiKey: String) : Event
        data object SavePressed : Event
        data object BackPressed : Event
    }
}