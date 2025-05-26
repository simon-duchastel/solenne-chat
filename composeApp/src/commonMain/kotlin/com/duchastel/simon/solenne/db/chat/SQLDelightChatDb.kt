package com.duchastel.simon.solenne.db.chat

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.duchastel.simon.solenne.db.DatabaseWrapper
import com.duchastel.simon.solenne.db.Message
import com.duchastel.simon.solenne.dispatchers.IODispatcher
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * SQLDelight implementation of [ChatMessageDb].
 * Uses SQLDelight to persist chat messages and conversations.
 */
@Inject
@OptIn(ExperimentalTime::class)
class SQLDelightChatDb(
    private val database: DatabaseWrapper,
    private val dispatcher: CoroutineDispatcher = IODispatcher,
) : ChatMessageDb {

    override fun getConversationIds(): Flow<List<String>> {
        return flow {
            val conversationFlow = database
                .execute { conversationQueries.getConversations() }
                .asFlow()
                .mapToList(dispatcher)
            emitAll(conversationFlow)
        }
    }

    override suspend fun createConversation(conversationId: String): String {
        val timestamp = Clock.System.now().toEpochMilliseconds()
        withContext(dispatcher) {
            database.execute { conversationQueries.insertConversation(conversationId, timestamp) }
        }
        return conversationId
    }

    override fun getMessagesForConversation(conversationId: String): Flow<List<DbMessage>> {
        return flow {
            val messagesFlow = database
                .execute { messageQueries.getMessagesForConversation(conversationId) }
                .asFlow()
                .mapToList(dispatcher)
                .map { messages ->
                    messages.map { message -> messageToDbMessage(message) }
                }
            emitAll(messagesFlow)
        }
    }

    override suspend fun writeMessage(message: DbMessage): DbMessage {
        return withContext(dispatcher) {
            when (val content = message.content) {
                is DbMessageContent.Text -> {
                    database.execute {
                        messageQueries.insertMessage(
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
                }

                is DbMessageContent.ToolUse -> {
                    val argumentsJson = Json.encodeToString(content.argumentsSupplied)
                    database.execute {
                        messageQueries.insertMessage(
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
            }

            message
        }
    }

    override suspend fun updateMessageContent(
        messageId: String,
        conversationId: String,
        newContent: DbMessageContent
    ): DbMessage? {
        return withContext(dispatcher) {
            // Get the current message to check its type
            val currentMessages = database
                .execute { messageQueries.getMessagesForConversation(conversationId) }
                .asFlow()
                .mapToList(dispatcher)

            val firstBatch = currentMessages.first()
            val currentMessage = firstBatch
                .find { it.id == messageId }
                ?.let { messageToDbMessage(it) }
                ?: return@withContext null

            // Ensure content types match
            if ((currentMessage.content is DbMessageContent.Text && newContent !is DbMessageContent.Text) ||
                (currentMessage.content is DbMessageContent.ToolUse && newContent !is DbMessageContent.ToolUse)
            ) {
                return@withContext null
            }

            when (newContent) {
                is DbMessageContent.Text -> {
                    database.execute {
                        messageQueries.updateTextMessageContent(
                            text_content = newContent.text,
                            id = messageId,
                            conversation_id = conversationId
                        )
                    }
                }

                is DbMessageContent.ToolUse -> {
                    val argumentsJson = Json.encodeToString(newContent.argumentsSupplied)
                    database.execute {
                        messageQueries.updateToolUseMessageContent(
                            tool_name = newContent.toolName,
                            mcp_server_id = newContent.mcpServerId,
                            arguments_supplied = argumentsJson,
                            result_text = newContent.result?.text,
                            result_is_error = newContent.result?.isError?.let { if (it) 1L else 0L },
                            id = messageId,
                            conversation_id = conversationId
                        )
                    }
                }
            }

            DbMessage(
                id = currentMessage.id,
                conversationId = currentMessage.conversationId,
                author = currentMessage.author,
                timestamp = currentMessage.timestamp,
                content = newContent
            )
        }
    }

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
