package org.duchastel.simon.solenne.screens.chat

import androidx.compose.runtime.Composable
import com.slack.circuit.runtime.presenter.Presenter

class ChatPresenter : Presenter<ChatScreen.State> {
    @Composable
    override fun present(): ChatScreen.State {
        return ChatScreen.State()
    }
}
