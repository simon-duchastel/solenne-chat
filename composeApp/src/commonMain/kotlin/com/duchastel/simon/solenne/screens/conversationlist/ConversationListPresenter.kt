package com.duchastel.simon.solenne.screens.conversationlist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.duchastel.simon.solenne.data.chat.ChatMessageRepository
import com.duchastel.simon.solenne.screens.chat.ChatScreen
import com.duchastel.simon.solenne.screens.conversationlist.ConversationListScreen.Event.ConversationClicked
import com.duchastel.simon.solenne.screens.conversationlist.ConversationListScreen.Event.NewConversationClicked
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.Inject
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ConversationListPresenter @Inject constructor(
    @Assisted private val navigator: Navigator,
    private val chatRepository: ChatMessageRepository,
) : Presenter<ConversationListScreen.State> {

    @Composable
    override fun present(): ConversationListScreen.State {
        val coroutineScope = rememberCoroutineScope()
        val conversations by remember {
            chatRepository.getAvailableConversationsFlow().map { conversations ->
                conversations
                    .map { it.id }
                    .toPersistentList()
            }
        }.collectAsState(persistentListOf())

        return ConversationListScreen.State(
            conversations = conversations,
        ) { event ->
            when (event) {
                is ConversationClicked -> {
                    navigator.goTo(ChatScreen(event.conversationId))
                }
                is NewConversationClicked -> {
                    coroutineScope.launch {
                        chatRepository.createNewConversation()
                    }
                }
            }
        }
    }

    @AssistedFactory
    fun interface Factory {
        fun create(navigator: Navigator): ConversationListPresenter
    }
}