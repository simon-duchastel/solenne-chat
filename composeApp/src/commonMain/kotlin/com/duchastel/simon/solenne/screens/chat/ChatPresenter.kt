package com.duchastel.simon.solenne.screens.chat

import androidx.compose.runtime.Composable
import com.duchastel.simon.solenne.fakes.ChatMessagesFake
import com.slack.circuit.runtime.presenter.Presenter
import kotlinx.collections.immutable.toPersistentList

class ChatPresenter : Presenter<ChatScreen.State> {
    @Composable
    override fun present(): ChatScreen.State {
        return ChatScreen.State(
            messages = ChatMessagesFake.chatMessages.toPersistentList(),
        )
    }
}
