package org.duchastel.simon.solenne.screens.chat

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.screen.Screen
import com.slack.circuit.runtime.ui.Ui
import org.duchastel.simon.solenne.parcel.Parcelize

@Parcelize
data object ChatScreen: Screen {
    data class State(
        val name: String,
        val eventSink: (Event) -> Unit = {},
    ): CircuitUiState

    sealed interface Event : CircuitUiEvent
}

class ChatPresenter: Presenter<ChatScreen.State> {
    @Composable
    override fun present(): ChatScreen.State {
        return ChatScreen.State(name = "Simon")
    }
}

class ChatUi: Ui<ChatScreen.State> {
    @Composable
    override fun Content(state: ChatScreen.State, modifier: Modifier) {
        Text(text = "Hello ${state.name}", modifier = modifier)
    }
}