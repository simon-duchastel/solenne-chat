package com.duchastel.simon.solenne.fakes

import com.duchastel.simon.solenne.db.chat.ChatMessageDb
import com.duchastel.simon.solenne.db.chat.DbMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

internal class FakeChatMessageDb: ChatMessageDb {
    private val messages = MutableStateFlow<Map<String, List<DbMessage>>>(emptyMap())

    override fun getMessagesForConversation(conversationId: String): Flow<List<DbMessage>> {
        return messages.map { it[conversationId].orEmpty() }
    }

    override fun writeMessage(message: DbMessage) {
        val currentMessages = messages.value[message.conversationId].orEmpty()
        messages.value += (message.conversationId to (currentMessages + message))
    }
}
