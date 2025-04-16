package com.duchastel.simon.solenne.screens.chat

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.duchastel.simon.solenne.data.ChatMessageRepository
import com.slack.circuit.runtime.presenter.Presenter
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.Inject
import kotlinx.collections.immutable.persistentListOf

class ChatPresenter @Inject constructor(
    private val repository: ChatMessageRepository,
    @Assisted private val screen: ChatScreen
) : Presenter<ChatScreen.State> {

    @Composable
    override fun present(): ChatScreen.State {
        val messagesFlow = repository.getMessagesForConversation(screen.conversationId)
        val messages by messagesFlow.collectAsState(initial = persistentListOf())
        return ChatScreen.State(
            messages = messages,
        )
    }

    @AssistedFactory
    fun interface Factory {
        fun create(screen: ChatScreen): ChatPresenter
    }
}