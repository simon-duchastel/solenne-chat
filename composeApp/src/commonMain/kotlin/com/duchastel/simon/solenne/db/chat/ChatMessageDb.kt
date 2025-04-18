package com.duchastel.simon.solenne.db.chat

import kotlinx.coroutines.flow.Flow

interface ChatMessageDb {
    fun getMessagesForConversation(
        conversationId: String,
    ): Flow<List<DbMessage>>

    suspend fun writeMessage(
        message: DbMessage
    )

    suspend fun updateMessageContent(
        conversationId: String,
        id: String,
        newContent: String,
    )
}

data class DbMessage(
    val id: String,
    val conversationId: String,
    val content: String,
    val author: Long, // 0 = User, 1 = AI
    val timestamp: Long,
)