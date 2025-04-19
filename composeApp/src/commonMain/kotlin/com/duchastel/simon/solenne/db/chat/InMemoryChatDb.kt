package com.duchastel.simon.solenne.db.chat

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

@Inject
class InMemoryChatDb: ChatMessageDb {
    override fun getMessagesForConversation(conversationId: String): Flow<List<DbMessage>> {
        val messagesForConversation = messages[conversationId] ?: emptyList()
        return snapshotFlow { messagesForConversation }.distinctUntilChanged()
    }

    override suspend fun writeMessage(message: DbMessage) {
        val currentMessages = messages[message.conversationId] ?: emptyList()
        val newMessages = currentMessages + message
        messages += (message.conversationId to newMessages)
    }

    override suspend fun updateMessageContent(
        messageId: String,
        conversationId: String,
        newContent: String,
    ) {
        val currentMessages = messages[conversationId]
            ?: error("Could not find conversation $conversationId")
        val updatedMessages = currentMessages.map {
            if (messageId == it.id) it.copy(content = newContent) else it
        }
        messages += (conversationId to updatedMessages)
    }

    companion object {
        private var messages by mutableStateOf<Map<String, List<DbMessage>>>(emptyMap())
    }
}