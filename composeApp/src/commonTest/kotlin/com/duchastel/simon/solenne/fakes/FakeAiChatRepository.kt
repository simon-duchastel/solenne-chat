package com.duchastel.simon.solenne.fakes

import com.duchastel.simon.solenne.data.ai.AiChatRepository
import com.duchastel.simon.solenne.data.chat.ChatMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeAiChatRepository(
    initialMessages: Map<String, List<ChatMessage>> = emptyMap(),
) : AiChatRepository {
    private val conversations = MutableStateFlow(initialMessages)

    override fun getMessageFlowForConversation(conversationId: String): Flow<List<ChatMessage>> {
        return conversations.map { it[conversationId] ?: emptyList() }
    }

    override suspend fun sendTextMessageFromUserToConversation(
        conversationId: String,
        text: String,
    ) = Unit
}