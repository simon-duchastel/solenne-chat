package com.duchastel.simon.solenne.screens.modelselector

import androidx.compose.runtime.Immutable
import com.duchastel.simon.solenne.parcel.Parcelize
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.screen.Screen
import kotlinx.collections.immutable.PersistentList

@Parcelize
data object ModelProviderSelectorScreen : Screen {
    @Immutable
    data class State(
        val models: PersistentList<UiModelProvider>,
        val eventSink: (Event) -> Unit = {},
    ) : CircuitUiState

    sealed interface Event : CircuitUiEvent {
        data class ModelSelected(val modelProvider: UiModelProvider) : Event
        data object BackPressed : Event
    }
}

sealed interface UiModelProvider {
    data object Gemini : UiModelProvider
    data object OpenAI : UiModelProvider
    data object Anthropic : UiModelProvider
    data object DeepSeek : UiModelProvider
    data object Grok : UiModelProvider
    data class Other(val name: String?) : UiModelProvider // null if no name specified yet
}