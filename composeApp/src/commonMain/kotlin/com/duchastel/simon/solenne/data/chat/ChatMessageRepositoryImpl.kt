package com.duchastel.simon.solenne.data.chat

import com.duchastel.simon.solenne.db.chat.ChatMessageDb
import com.duchastel.simon.solenne.db.chat.DbMessage
import com.duchastel.simon.solenne.dispatchers.IODispatcher
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.Dispatchers
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
                query.map(DbMessage::toChatMessage)
            }
    }

    override suspend fun modifyMessageFromConversation(
        conversationId: String,
        messageId: String,
        newText: String,
    ): String {
        return withContext(IODispatcher) {
            chatMessageDb.updateMessageContent(
                messageId = messageId,
                conversationId = conversationId,
                newContent = newText,
            )
            return@withContext messageId
        }
    }

    @OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
    override suspend fun addMessageToConversation(
        conversationId: String,
        author: MessageAuthor,
        text: String,
    ): String {
        return withContext(IODispatcher) {
            val messageId = Uuid.random().toHexString()
            chatMessageDb.writeMessage(
                DbMessage(
                    id = messageId,
                    conversationId = conversationId,
                    author = when (author) {
                        MessageAuthor.User -> 0L
                        MessageAuthor.AI -> 1L
                        MessageAuthor.System -> 2L
                    },
                    content = text.trim(),
                    timestamp = Clock.System.now().toEpochMilliseconds(),
                )
            )
            return@withContext messageId
        }
    }
}

fun DbMessage.toChatMessage(): ChatMessage {
    return ChatMessage(
        id = id,
        text = content,
        author = when (author) {
            0L -> MessageAuthor.User
            1L -> MessageAuthor.AI
            2L -> MessageAuthor.System
            else -> error("Unknown author received for GetMessagesForConversation[$this] - $author")
        }
    )
}