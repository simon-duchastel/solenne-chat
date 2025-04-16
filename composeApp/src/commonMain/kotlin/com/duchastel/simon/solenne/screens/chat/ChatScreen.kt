package com.duchastel.simon.solenne.screens.chat

import androidx.compose.runtime.Immutable
import com.duchastel.simon.solenne.parcel.Parcelize
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.screen.Screen
import kotlinx.collections.immutable.PersistentList

@Parcelize
data object ChatScreen: Screen {

    @Immutable
    data class State(
        val messages: PersistentList<ChatMessage>,
        val eventSink: (Event) -> Unit = {},
    ): CircuitUiState

    @Immutable
    data class ChatMessage(val text: String, val isUser: Boolean)

    sealed interface Event : CircuitUiEvent
}