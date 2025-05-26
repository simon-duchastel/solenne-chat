package com.duchastel.simon.solenne.fakes

import com.duchastel.simon.solenne.db.chat.ChatMessageDb
import com.duchastel.simon.solenne.db.chat.DbMessage
import com.duchastel.simon.solenne.db.chat.DbMessageContent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

internal class FakeChatMessageDb(
    initialMessages: Map<String, List<DbMessage>> = emptyMap()
): ChatMessageDb {
    private val messages = MutableStateFlow(initialMessages)

    override fun getConversationIds(): Flow<List<String>> {
        return messages.map { it.keys.toList() }
    }

    override suspend fun createConversation(conversationId: String): String {
        if (!messages.value.containsKey(conversationId)) {
            messages.value += (conversationId to emptyList())
        }
        return conversationId
    }

    override fun getMessagesForConversation(conversationId: String): Flow<List<DbMessage>> {
        return messages.map { it[conversationId].orEmpty() }
    }

    override suspend fun writeMessage(message: DbMessage): DbMessage {
        val currentMessages = messages.value[message.conversationId].orEmpty()
        messages.value += (message.conversationId to (currentMessages + message))
        return message
    }

    override suspend fun updateMessageContent(
        messageId: String,
        conversationId: String,
        newContent: DbMessageContent
    ): DbMessage? {
        val currentMessages = messages.value[conversationId].orEmpty()
        val messageToUpdate = currentMessages.find { it.id == messageId }

        if (messageToUpdate == null) {
            return null
        }

        val updatedMessage = messageToUpdate.copy(content = newContent)
        val updatedMessages = currentMessages.map { msg ->
            if (msg.id == messageId) updatedMessage else msg
        }

        messages.value += (conversationId to updatedMessages)
        return updatedMessage
    }
}
