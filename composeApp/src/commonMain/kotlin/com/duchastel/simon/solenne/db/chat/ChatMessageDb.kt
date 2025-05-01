package com.duchastel.simon.solenne.db.chat

import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.JsonElement

/**
 * Interface for the chat message database.
 * Responsible for persisting chat messages and conversations.
 */
interface ChatMessageDb {
    /**
     * Gets all conversations. Returns the list of conversation IDs.
     */
    fun getConversationIds(): Flow<List<String>>

    /**
     * Creates a new conversation with the given ID. Returns the ID of the new conversation if
     * successful, null otherwise.
     */
    suspend fun createConversation(conversationId: String): String

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
     * Updates the [DbMessageContent] content of a message for a given conversation.
     *
     * The message corresponding to the given [messageId] must be of the same [DbMessageContent],
     * type, otherwise this method will fail and return null.
     *
     * Returns the successfully updated message if it succeeded, false otherwise.
     */
    suspend fun updateMessageContent(
        messageId: String,
        conversationId: String,
        newContent: DbMessageContent,
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
        val mcpServerId: String,
        val argumentsSupplied: Map<String, JsonElement>,
        val result: ToolResult?,
    ) : DbMessageContent {
        data class ToolResult(
            val text: String,
            val isError: Boolean,
        )
    }
}