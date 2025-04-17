package com.duchastel.simon.solenne.data

import com.duchastel.simon.solenne.fakes.ChatMessagesFake
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

interface ChatMessageRepository {
    fun getMessagesForConversation(conversationId: String): Flow<List<ChatMessage>>
}

@Inject
class ChatMessageRepositoryImpl: ChatMessageRepository {
    override fun getMessagesForConversation(conversationId: String): Flow<List<ChatMessage>> {
        return flowOf(ChatMessagesFake.chatMessages)
    }
}
