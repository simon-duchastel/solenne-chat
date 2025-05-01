package com.duchastel.simon.solenne.screens.chat

import androidx.compose.runtime.Immutable
import com.duchastel.simon.solenne.parcel.Parcelize
import com.duchastel.simon.solenne.ui.model.UIChatMessage
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
        val messages: PersistentList<UIChatMessage>,
        val sendButtonEnabled: Boolean,
        val textInput: String,
        val apiKey: String,
        val eventSink: (Event) -> Unit = {},
    ): CircuitUiState

    sealed interface Event : CircuitUiEvent {
        data class TextInputChanged(val text: String): Event
        data class SendMessage(val text: String): Event
        data class ApiKeyChanged(val apiKey: String) : Event
        data class ApiKeySubmitted(val apiKey: String) : Event
        data object BackPressed : Event
    }
}