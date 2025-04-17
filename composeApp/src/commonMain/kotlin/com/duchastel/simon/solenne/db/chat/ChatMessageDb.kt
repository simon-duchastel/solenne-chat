package com.duchastel.simon.solenne.db.chat

import kotlinx.coroutines.flow.Flow

interface ChatMessageDb {
    fun getMessagesForConversation(
        conversation: String,
    ): Flow<List<GetMessagesForConversation>>
}

data class GetMessagesForConversation(
    val id: String,
    val conversationId: String,
    val content: String,
    val author: Long, // 0 = User, 1 = AI
    val timestamp: Long,
)