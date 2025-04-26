package com.duchastel.simon.solenne.data.chat

import com.duchastel.simon.solenne.data.tools.McpServer
import com.duchastel.simon.solenne.db.chat.ChatMessageDb
import com.duchastel.simon.solenne.db.chat.DbMessage
import com.duchastel.simon.solenne.db.chat.DbMessageContent
import com.duchastel.simon.solenne.dispatchers.IODispatcher
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonElement
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class ChatMessageRepositoryImpl @Inject constructor(
    private val chatMessageDb: ChatMessageDb,
) : ChatMessageRepository {

    override fun getMessageFlowForConversation(conversationId: String): Flow<List<ChatMessage>> {
        return chatMessageDb.getMessagesForConversation(conversationId)
            .distinctUntilChanged()
            .map { query ->
                query.mapNotNull(DbMessage::toChatMessage) // drop unparseable messages
            }
    }

    override suspend fun modifyMessageFromConversation(
        conversationId: String,
        messageId: String,
        updatedText: String,
    ): ChatMessage? {
        return withContext(IODispatcher) {
            val dbMessage = chatMessageDb.updateMessageContent(
                messageId = messageId,
                conversationId = conversationId,
                newContent = DbMessageContent.Text(updatedText),
            )
            return@withContext dbMessage?.toChatMessage()
        }
    }

    @OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
    override suspend fun addTextMessageToConversation(
        conversationId: String,
        author: MessageAuthor,
        text: String,
    ): ChatMessage? {
        return withContext(IODispatcher) {
            val messageId = Uuid.random().toHexString()
            val newMessage = chatMessageDb.writeMessage(
                DbMessage(
                    id = messageId,
                    conversationId = conversationId,
                    author = author.asLong(),
                    content = DbMessageContent.Text(text.trim()),
                    timestamp = Clock.System.now().toEpochMilliseconds(),
                )
            )
            return@withContext newMessage.toChatMessage()
        }
    }

    @OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
    override suspend fun addToolUseToConversation(
        conversationId: String,
        mcpServer: McpServer,
        toolName: String,
        argumentsSupplied: Map<String, JsonElement>,
    ): ChatMessage? {
        return withContext(IODispatcher) {
            val newMessage = chatMessageDb.writeMessage(
                DbMessage(
                    id = Uuid.random().toHexString(),
                    conversationId = conversationId,
                    author = 1L, // AI is always the author of tool uses
                    content = DbMessageContent.ToolUse(
                        toolName = toolName,
                        mcpServerId = mcpServer.id,
                        argumentsSupplied = argumentsSupplied,
                        result = null,
                    ),
                    timestamp = Clock.System.now().toEpochMilliseconds(),
                )
            )
            return@withContext newMessage.toChatMessage()
        }
    }

    override suspend fun addToolUseResultToConversation(
        conversationId: String,
        messageId: String,
        toolResult: ChatMessage.ToolUse.ToolResult,
    ): ChatMessage? {
        return withContext(IODispatcher) {
            val existingMessage = chatMessageDb.getMessagesForConversation(conversationId)
                .first()
                .find { it.id == messageId && it.content is DbMessageContent.ToolUse }

            // tool use results must correspond to an existing tool use
            if (existingMessage == null) return@withContext null

            // this cast is safe because we checked in the find block
            val toolUse = existingMessage.content as DbMessageContent.ToolUse
            val updatedContent = DbMessageContent.ToolUse(
                toolName = toolUse.toolName,
                mcpServerId = toolUse.mcpServerId,
                argumentsSupplied = toolUse.argumentsSupplied,
                result = DbMessageContent.ToolUse.ToolResult(
                    text = toolResult.text,
                    isError = toolResult.isError
                )
            )

            val dbMessage = chatMessageDb.updateMessageContent(
                messageId = messageId,
                conversationId = conversationId,
                newContent = updatedContent
            )

            return@withContext dbMessage?.toChatMessage()
        }
    }
}

/**
 * Helper function to parse a [DbMessage] into a [ChatMessage].
 * Returns null if an error occurred during parsing.
 */
private fun DbMessage.toChatMessage(): ChatMessage? {
    return when (val content = this.content) {
        is DbMessageContent.Text -> {
            ChatMessage.Text(
                id = id,
                text = content.text,
                author = author.asAuthor() ?: return null,
            )
        }
        is DbMessageContent.ToolUse -> {
            ChatMessage.ToolUse(
                id = id,
                toolName = content.toolName,
                argumentsSupplied = content.argumentsSupplied,
                result = content.result?.let {
                    ChatMessage.ToolUse.ToolResult(
                        text = it.text,
                        isError = it.isError
                    )
                }
            )
        }
    }
}

/**
 * Helper function to parse a [Long] into a [MessageAuthor].
 * Returns null if an error occurred during parsing.
 */
private fun Long.asAuthor(): MessageAuthor? {
    return when (this) {
        0L -> MessageAuthor.User
        1L -> MessageAuthor.AI
        else -> null
    }
}

/**
 * Helper function to serialize a [MessageAuthor] into a [Long].
 */
private fun MessageAuthor.asLong(): Long {
    return when (this) {
        MessageAuthor.User -> 0L
        MessageAuthor.AI -> 1L
    }
}