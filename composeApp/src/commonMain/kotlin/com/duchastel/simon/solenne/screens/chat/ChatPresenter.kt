package com.duchastel.simon.solenne.screens.chat

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.duchastel.simon.solenne.data.ChatMessage
import com.duchastel.simon.solenne.data.ChatMessageRepository
import com.duchastel.simon.solenne.ui.model.UIChatMessage
import com.duchastel.simon.solenne.ui.model.toUIChatMessage
import com.slack.circuit.runtime.presenter.Presenter
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.Inject
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.mutate
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.map

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