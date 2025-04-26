package com.duchastel.simon.solenne.data.chat

import com.duchastel.simon.solenne.db.chat.ChatMessageDb
import com.duchastel.simon.solenne.db.chat.DbMessage
import com.duchastel.simon.solenne.db.chat.DbMessageContent
import com.duchastel.simon.solenne.dispatchers.IODispatcher
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
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
        newText: String,
    ): ChatMessage? {
        return withContext(IODispatcher) {
            val dbMessage = chatMessageDb.updateTextMessageContent(
                messageId = messageId,
                conversationId = conversationId,
                newContent = DbMessageContent.Text(newText),
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
                    author = when (author) {
                        MessageAuthor.User -> 0L
                        MessageAuthor.AI -> 1L
                    },
                    content = DbMessageContent.Text(text.trim()),
                    timestamp = Clock.System.now().toEpochMilliseconds(),
                )
            )
            return@withContext newMessage.toChatMessage()
        }
    }
}

/**
 * Helper function to parse a [DbMessage] into a [ChatMessage].
 * Returns null if an error occurred during parsing.
 */
private fun DbMessage.toChatMessage(): ChatMessage? {
    return when (this.content) {
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
                author = author.asAuthor() ?: return null,
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