package com.duchastel.simon.solenne.fakes

import com.duchastel.simon.solenne.data.chat.ChatMessage
import com.duchastel.simon.solenne.data.chat.ChatMessageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeChatMessageRepository(
    private val messageFlow: Flow<List<ChatMessage>> = flowOf(ChatMessagesFake.chatMessages)
): ChatMessageRepository {
    override fun getMessagesForConversation(conversationId: String): Flow<List<ChatMessage>> = messageFlow
}