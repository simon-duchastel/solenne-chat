package com.duchastel.simon.solenne.screens.chat

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.duchastel.simon.solenne.data.ai.AIModelScope
import com.duchastel.simon.solenne.data.ai.AIModelScope.GeminiModelScope
import com.duchastel.simon.solenne.data.ai.AiChatRepository
import com.duchastel.simon.solenne.data.chat.ChatMessage
import com.duchastel.simon.solenne.ui.model.toUIChatMessage
import com.slack.circuit.runtime.presenter.Presenter
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.Inject
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

class ChatPresenter @Inject constructor(
    private val aiChatRepository: AiChatRepository,
    @Assisted private val screen: ChatScreen
) : Presenter<ChatScreen.State> {

    @Composable
    override fun present(): ChatScreen.State {
        var aiModelScope: AIModelScope? by remember { mutableStateOf(null) }

        var textInput by rememberSaveable { mutableStateOf("") }
        var userApiKey by rememberSaveable { mutableStateOf("") }
        val coroutineScope = rememberCoroutineScope()

        val scope = aiModelScope
        val messages by aiChatRepository.getMessageFlowForConversation(screen.conversationId)
            .collectAsState(initial = emptyList())

        return ChatScreen.State(
            sendButtonEnabled = aiModelScope != null && textInput.isNotBlank(),
            textInput = textInput,
            apiKey = userApiKey,
            messages = messages.map(ChatMessage::toUIChatMessage).toPersistentList(),
        ) { event ->
            when (event) {
                is ChatScreen.Event.SendMessage -> coroutineScope.launch {
                    textInput = ""
                    scope ?: return@launch
                    aiChatRepository.sendTextMessageFromUserToConversation(
                        scope,
                        screen.conversationId,
                        event.text
                    )
                }
                is ChatScreen.Event.TextInputChanged -> {
                    textInput = event.text
                }
                is ChatScreen.Event.ApiKeyChanged -> {
                    userApiKey = event.apiKey
                }
                is ChatScreen.Event.ApiKeySubmitted -> {
                    aiModelScope = GeminiModelScope(userApiKey)
                }
            }
        }
    }

    @AssistedFactory
    fun interface Factory {
        fun create(screen: ChatScreen): ChatPresenter
    }
}