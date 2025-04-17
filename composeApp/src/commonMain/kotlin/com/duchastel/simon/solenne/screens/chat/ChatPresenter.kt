package com.duchastel.simon.solenne.screens.chat

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.duchastel.simon.solenne.data.chat.ChatMessage
import com.duchastel.simon.solenne.data.chat.ChatMessageRepository
import com.duchastel.simon.solenne.ui.model.toUIChatMessage
import com.slack.circuit.runtime.presenter.Presenter
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.Inject
import kotlinx.collections.immutable.toPersistentList

class ChatPresenter @Inject constructor(
    private val repository: ChatMessageRepository,
    @Assisted private val screen: ChatScreen
) : Presenter<ChatScreen.State> {

    @Composable
    override fun present(): ChatScreen.State {
        val messages by repository.getMessagesForConversation(screen.conversationId)
            .collectAsState(initial = listOf())

        return ChatScreen.State(
            messages = messages.map(ChatMessage::toUIChatMessage).toPersistentList(),
        )
    }

    @AssistedFactory
    fun interface Factory {
        fun create(screen: ChatScreen): ChatPresenter
    }
}