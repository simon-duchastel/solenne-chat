package com.duchastel.simon.solenne.screens.chat

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.duchastel.simon.solenne.data.chat.ChatMessageRepository
import com.duchastel.simon.solenne.data.chat.models.ChatMessage
import com.duchastel.simon.solenne.data.tools.McpRepository
import com.duchastel.simon.solenne.data.tools.McpServer
import com.duchastel.simon.solenne.screens.chat.ChatScreen.Event.ApiKeyChanged
import com.duchastel.simon.solenne.screens.chat.ChatScreen.Event.ApiKeySubmitted
import com.duchastel.simon.solenne.screens.chat.ChatScreen.Event.BackPressed
import com.duchastel.simon.solenne.screens.chat.ChatScreen.Event.SendMessage
import com.duchastel.simon.solenne.screens.chat.ChatScreen.Event.TextInputChanged
import com.duchastel.simon.solenne.ui.model.UIChatMessage
import com.duchastel.simon.solenne.ui.model.toUIChatMessage
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.Inject
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ChatPresenter @Inject constructor(
    @Assisted private val navigator: Navigator,
    @Assisted private val screen: ChatScreen,
    private val chatRepository: ChatMessageRepository,
    private val aiChatRepository: AiChatRepository,
    private val mcpRepository: McpRepository,
) : Presenter<ChatScreen.State> {

    @Composable
    override fun present(): ChatScreen.State {
        var aiModelScope: AIModelScope? by remember { mutableStateOf(null) }

        var textInput by rememberSaveable { mutableStateOf("") }
        var userApiKey by rememberSaveable { mutableStateOf("") }
        val coroutineScope = rememberCoroutineScope()

        var server: McpServer? by remember { mutableStateOf(null) }

        val scope = aiModelScope
        val messages by remember {
            chatRepository.getMessageFlowForConversation(screen.conversationId)
        }.collectAsState(initial = emptyList())

        val serverStatus: String by remember(server) {
            if (server != null) {
                mcpRepository.serverStatusFlow().map { serverStatus ->
                    serverStatus.firstOrNull { it.mcpServer == server }?.status?.toString() ?: "Disconnected"
                }
            } else {
                flowOf("Disconnected")
            }
        }.collectAsState("Disconnected")

        LaunchedEffect(Unit) {
            server = mcpRepository.addServer(
                name = "Lifx",
                connection = McpServer.Connection.Sse(
                    url = "http://10.0.2.2:3000"
                )
            )?.mcpServer?.apply {
                mcpRepository.connect(this)
            }
        }

        return ChatScreen.State(
            sendButtonEnabled = aiModelScope != null && textInput.isNotBlank(),
            textInput = textInput,
            apiKey = userApiKey,
            messages = messages.map(ChatMessage::toUIChatMessage)
                .plus(UIChatMessage(
                    text = serverStatus,
                    isUser = false,
                    id = "123",
                ))
                .toPersistentList(),
        ) { event ->
            when (event) {
                is BackPressed -> {
                    navigator.pop()
                }
                is SendMessage -> coroutineScope.launch {
                    textInput = ""
                    scope ?: return@launch
                    aiChatRepository.sendTextMessageFromUserToConversation(
                        scope,
                        screen.conversationId,
                        event.text
                    )
                }
                is TextInputChanged -> {
                    textInput = event.text
                }
                is ApiKeyChanged -> {
                    userApiKey = event.apiKey
                }
                is ApiKeySubmitted -> {
                    aiModelScope = GeminiModelScope(userApiKey)
                }
            }
        }
    }

    @AssistedFactory
    fun interface Factory {
        fun create(screen: ChatScreen, navigator: Navigator): ChatPresenter
    }
}