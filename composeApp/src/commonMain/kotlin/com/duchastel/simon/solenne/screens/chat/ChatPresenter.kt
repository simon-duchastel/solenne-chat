package com.duchastel.simon.solenne.screens.chat

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.duchastel.simon.solenne.data.ai.AiChatRepository
import com.duchastel.simon.solenne.data.chat.ChatMessage
import com.duchastel.simon.solenne.ui.model.toUIChatMessage
import com.slack.circuit.runtime.presenter.Presenter
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.Inject
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.launch

class ChatPresenter @Inject constructor(
    private val aiChatRepository: AiChatRepository,
    @Assisted private val screen: ChatScreen
) : Presenter<ChatScreen.State> {

    @Composable
    override fun present(): ChatScreen.State {
        val messages by aiChatRepository.getMessageFlowForConversation(screen.conversationId)
            .collectAsState(initial = emptyList())
        var textInput by rememberSaveable { mutableStateOf("") }
        val coroutineScope = rememberCoroutineScope()

        return ChatScreen.State(
            saveButtonEnabled = textInput.isNotBlank(),
            textInput = textInput,
            messages = messages.map(ChatMessage::toUIChatMessage).toPersistentList(),
        ) {
            when (it) {
                is ChatScreen.Event.SendMessage -> coroutineScope.launch {
                    textInput = ""
                    aiChatRepository.sendTextMessageFromUserToConversation(
                        screen.conversationId,
                        it.text
                    )
                }
                is ChatScreen.Event.TextInputChanged -> {
                    textInput = it.text
                }
            }
        }
    }

    @AssistedFactory
    fun interface Factory {
        fun create(screen: ChatScreen): ChatPresenter
    }
}