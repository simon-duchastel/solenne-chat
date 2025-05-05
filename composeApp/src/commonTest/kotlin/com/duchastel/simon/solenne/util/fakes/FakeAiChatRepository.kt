package com.duchastel.simon.solenne.util.fakes

import com.duchastel.simon.solenne.data.ai.AIModelProvider
import com.duchastel.simon.solenne.data.ai.AIModelProviderStatus
import com.duchastel.simon.solenne.data.ai.AIModelScope
import com.duchastel.simon.solenne.data.ai.AIProviderConfig
import com.duchastel.simon.solenne.data.ai.AiChatRepository
import com.duchastel.simon.solenne.data.chat.models.ChatMessage
import com.duchastel.simon.solenne.data.chat.models.MessageAuthor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeAiChatRepository(
    initialMessages: Map<String, List<ChatMessage>> = emptyMap(),
    availableModels: List<AIModelProviderStatus<*>> = listOf(
        AIModelProviderStatus.Gemini(AIModelScope.GeminiModelScope("<<do-not-rely-on-this-id>>"))
    ),
) : AiChatRepository {
    private val conversations = MutableStateFlow(initialMessages)
    private val modelsFlow = MutableStateFlow(availableModels)
    private val receivedConfigs = mutableListOf<AIProviderConfig<*>>()

    fun getMessagesSent(conversationId: String): List<ChatMessage>? {
        return conversations.value[conversationId]
    }

    override fun getAvailableModelsFlow(): Flow<List<AIModelProviderStatus<*>>> = modelsFlow

    override fun <T : AIModelProvider> configureModel(config: AIProviderConfig<T>): AIModelProviderStatus<T>? {
        receivedConfigs.add(config)
        return null
    }

    fun getReceivedConfigs(): List<AIProviderConfig<*>> = receivedConfigs.toList()

    override suspend fun sendTextMessageFromUserToConversation(
        aiModelScope: AIModelScope,
        conversationId: String,
        text: String,
    ) {
        conversations.value = conversations.value.toMutableMap().apply {
            this[conversationId] = this[conversationId].orEmpty().toMutableList().apply {
                add(
                    ChatMessage.Text(
                        id = "do-not-rely-on-this-id",
                        text = text,
                        author = MessageAuthor.User
                    )
                )
            }
        }
    }
}