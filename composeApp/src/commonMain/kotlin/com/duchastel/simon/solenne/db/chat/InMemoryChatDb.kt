package com.duchastel.simon.solenne.db.chat

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import com.duchastel.simon.solenne.data.chat.MessageAuthor
import com.duchastel.simon.solenne.fakes.ChatMessagesFake
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
        messages + (message.conversationId to newMessages)
    }

    companion object {
        private var messages by mutableStateOf<Map<String, List<DbMessage>>>(
            mapOf(
                "123" to ChatMessagesFake.chatMessages.map {
                    DbMessage(
                        id = it.id,
                        conversationId = "123",
                        content = it.text,
                        author = if (it.author is MessageAuthor.User) 0L else 1L,
                       timestamp = 0L,
                    )
                },
            )
        )
    }
}