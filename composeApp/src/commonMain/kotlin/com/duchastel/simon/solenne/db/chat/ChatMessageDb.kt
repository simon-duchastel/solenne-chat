package com.duchastel.simon.solenne.db.chat

import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.JsonElement

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
     * Writes a [message] to the database for a given conversation.
     * Returns the successfully written message if it succeeded, false
     * otherwise.
     */
    suspend fun writeMessage(
        message: DbMessage
    ): DbMessage

    /**
     * Updates the [DbMessageContent.Text] content of a message for a given conversation.
     *
     * The message corresponding to the given [messageId] must be of type [DbMessageContent.Text],
     * otherwise this method will fail and return null.
     *
     * Returns the successfully updated message if it succeeded, false
     * otherwise.
     */
    suspend fun updateTextMessageContent(
        messageId: String,
        conversationId: String,
        newContent: DbMessageContent.Text,
    ): DbMessage?
}

data class DbMessage(
    val id: String,
    val conversationId: String,
    val content: DbMessageContent,
    val author: Long, // 0 = User, 1 = AI
    val timestamp: Long,
)

sealed interface DbMessageContent {
    data class Text(
        val text: String,
    ) : DbMessageContent

    data class ToolUse(
        val toolName: String,
        val argumentsSupplied: Map<String, JsonElement>,
    ) : DbMessageContent
}