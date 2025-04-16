package com.duchastel.simon.solenne.screens.chat

import androidx.compose.runtime.Immutable
import com.duchastel.simon.solenne.data.ChatMessage
import com.duchastel.simon.solenne.parcel.Parcelize
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.screen.Screen
import kotlinx.collections.immutable.PersistentList

@Parcelize
data class ChatScreen(
    val conversationId: String,
) : Screen {
    @Immutable
    data class State(
        val messages: PersistentList<ChatMessage>,
        val eventSink: (Event) -> Unit = {},
    ): CircuitUiState

    sealed interface Event : CircuitUiEvent
}