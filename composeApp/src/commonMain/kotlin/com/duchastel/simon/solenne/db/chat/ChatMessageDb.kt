package com.duchastel.simon.solenne.db.chat

import kotlinx.coroutines.flow.Flow

/**
 * Interface for the chat message database.
 * Responsible for persisting chat messages and conversations.
 */
interface ChatMessageDb {
    /**
     * Gets all messages for a given conversation.
     */
    fun getMessagesForConversation(
        conversationId: String,
    ): Flow<List<DbMessage>>

    /**
     * Writes a message to the database for a given conversation.
     */
    suspend fun writeMessage(
        message: DbMessage
    )

    /**
     * Updates the content of a message for a given conversation.
     */
    suspend fun updateMessageContent(
        messageId: String,
        conversationId: String,
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