package com.duchastel.simon.solenne.db.chat

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import dev.zacsweers.metro.Inject
import io.ktor.http.ContentType.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlin.time.Clock

/**
 * SQLDelight implementation of [ChatMessageDb].
 * Uses SQLDelight to persist chat messages and conversations.
 */
@Inject
class SQLDelightChatDb(
    private val database: ChatDatabase
) : ChatMessageDb {

    override fun getConversationIds(): Flow<List<String>> {
        return database.chatDatabaseQueries.getAllConversations()
            .asFlow()
            .mapToList(Dispatchers.Default)
    }

    override suspend fun createConversation(conversationId: String): String {
        val timestamp = Clock.System.now().toEpochMilliseconds()
        database.chatDatabaseQueries.insertConversation(conversationId, timestamp)
        return conversationId
    }

    override fun getMessagesForConversation(conversationId: String): Flow<List<DbMessage>> {
        return database.chatDatabaseQueries.getMessagesForConversation(conversationId)
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { messages ->
                messages.map { message -> messageToDbMessage(message) }
            }
    }

    override suspend fun writeMessage(message: DbMessage): DbMessage {
        when (val content = message.content) {
            is DbMessageContent.Text -> {
                database.chatDatabaseQueries.insertMessage(
                    id = message.id,
                    conversation_id = message.conversationId,
                    author = message.author,
                    timestamp = message.timestamp,
                    content_type = "text",
                    text_content = content.text,
                    tool_name = null,
                    mcp_server_id = null,
                    arguments_supplied = null,
                    result_text = null,
                    result_is_error = null
                )
            }
            is DbMessageContent.ToolUse -> {
                val argumentsJson = Json.encodeToString(content.argumentsSupplied)
                database.chatDatabaseQueries.insertMessage(
                    id = message.id,
                    conversation_id = message.conversationId,
                    author = message.author,
                    timestamp = message.timestamp,
                    content_type = "tool_use",
                    text_content = null,
                    tool_name = content.toolName,
                    mcp_server_id = content.mcpServerId,
                    arguments_supplied = argumentsJson,
                    result_text = content.result?.text,
                    result_is_error = content.result?.isError?.let { if (it) 1L else 0L }
                )
            }
        }
        
        return message
    }

    override suspend fun updateMessageContent(
        messageId: String,
        conversationId: String,
        newContent: DbMessageContent
    ): DbMessage? {
        // Get the current message to check its type
        val currentMessages = database.chatDatabaseQueries
            .getMessagesForConversation(conversationId)
            .executeAsList()

        val currentMessage = currentMessages
            .find { it.id == messageId }
            ?.let { messageToDbMessage(it) }
            ?: return null

        // Ensure content types match
        if ((currentMessage.content is DbMessageContent.Text && newContent !is DbMessageContent.Text) ||
            (currentMessage.content is DbMessageContent.ToolUse && newContent !is DbMessageContent.ToolUse)) {
            return null
        }
        
        when (newContent) {
            is DbMessageContent.Text -> {
                val rowsUpdated = database.chatDatabaseQueries.updateTextMessageContent(
                    text_content = newContent.text,
                    id = messageId,
                    conversation_id = conversationId
                )
                if (rowsUpdated == 0L) {
                    return null
                }
            }
            is DbMessageContent.ToolUse -> {
                val argumentsJson = Json.encodeToString(newContent.argumentsSupplied)
                val rowsUpdated = database.chatDatabaseQueries.updateToolUseMessageContent(
                    tool_name = newContent.toolName,
                    mcp_server_id = newContent.mcpServerId,
                    arguments_supplied = argumentsJson,
                    result_text = newContent.result?.text,
                    result_is_error = newContent.result?.isError?.let { if (it) 1L else 0L },
                    id = messageId,
                    conversation_id = conversationId
                )
                if (rowsUpdated == 0L) {
                    return null
                }
            }
        }
        
        return DbMessage(
            id = currentMessage.id,
            conversationId = currentMessage.conversationId,
            author = currentMessage.author,
            timestamp = currentMessage.timestamp,
            content = newContent
        )
    }

    /**
     * Converts a SQLDelight Message row to a DbMessage.
     */
    private fun messageToDbMessage(message: Message): DbMessage {
        val dbContent = when (message.content_type) {
            "text" -> DbMessageContent.Text(message.text_content!!)
            "tool_use" -> {
                val argumentsMap = if (message.arguments_supplied != null) {
                    Json.decodeFromString<Map<String, JsonElement>>(message.arguments_supplied)
                } else {
                    emptyMap()
                }

                val toolResult = if (message.result_text != null) {
                    DbMessageContent.ToolUse.ToolResult(
                        text = message.result_text,
                        isError = message.result_is_error == 1L
                    )
                } else {
                    null
                }
                
                DbMessageContent.ToolUse(
                    toolName = message.tool_name!!,
                    mcpServerId = message.mcp_server_id!!,
                    argumentsSupplied = argumentsMap,
                    result = toolResult
                )
            }
            else -> throw IllegalArgumentException("Unknown content type: ${message.content_type}")
        }
        
        return DbMessage(
            id = message.id,
            conversationId = message.conversation_id,
            author = message.author,
            timestamp = message.timestamp,
            content = dbContent
        )
    }
}