package com.duchastel.simon.solenne.screens.conversationlist

import androidx.compose.runtime.Immutable
import com.duchastel.simon.solenne.parcel.Parcelize
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.screen.Screen
import kotlinx.collections.immutable.PersistentList

@Parcelize
data object ConversationListScreen: Screen {

    @Immutable
    data class State(
        val conversations: PersistentList<String>,
        val eventSink: (Event) -> Unit = {},
    ) : CircuitUiState

    sealed interface Event : CircuitUiEvent {
        data class ConversationClicked(val conversationId: String) : Event
        data object NewConversationClicked : Event
    }
}