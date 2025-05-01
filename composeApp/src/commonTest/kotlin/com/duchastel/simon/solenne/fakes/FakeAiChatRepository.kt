package com.duchastel.simon.solenne.fakes

import com.duchastel.simon.solenne.data.ai.AIModelProvider
import com.duchastel.simon.solenne.data.ai.AIModelScope
import com.duchastel.simon.solenne.data.ai.AiChatRepository
import com.duchastel.simon.solenne.data.chat.models.ChatMessage
import com.duchastel.simon.solenne.data.chat.models.MessageAuthor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeAiChatRepository(
    initialMessages: Map<String, List<ChatMessage>> = emptyMap(),
    availableModels: List<AIModelProvider> = emptyList(),
) : AiChatRepository {
    private val conversations = MutableStateFlow(initialMessages)
    private val modelsFlow = MutableStateFlow(availableModels)

    fun getMessagesSent(conversationId: String): List<ChatMessage>? {
        return conversations.value[conversationId]
    }

    override fun getAvailableModelsFlow(): Flow<List<AIModelProvider>> = modelsFlow

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