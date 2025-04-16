package com.duchastel.simon.solenne.screens.chat

import com.duchastel.simon.solenne.parcel.Parcelize
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.screen.Screen

@Parcelize
data object ChatScreen: Screen {
    data class State(
        val messages: List<ChatMessage>,
        val eventSink: (Event) -> Unit = {},
    ): CircuitUiState

    data class ChatMessage(val text: String, val isUser: Boolean)

    sealed interface Event : CircuitUiEvent
}