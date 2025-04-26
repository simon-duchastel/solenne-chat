package com.duchastel.simon.solenne.db.chat

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import com.duchastel.simon.solenne.network.ai.gemini.Content
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

@Inject
class InMemoryChatDb: ChatMessageDb {
    override fun getMessagesForConversation(conversationId: String): Flow<List<DbMessage>> {
        return snapshotFlow {
            messages[conversationId] ?: emptyList()
        }.distinctUntilChanged()
    }

    override suspend fun writeMessage(message: DbMessage): DbMessage {
        val currentMessages = messages[message.conversationId] ?: emptyList()
        val newMessages = currentMessages + message
        messages += (message.conversationId to newMessages)

        return message
    }

    override suspend fun updateTextMessageContent(
        messageId: String,
        conversationId: String,
        newContent: DbMessageContent.Text,
    ): DbMessage? {
        val currentMessages = messages[conversationId] ?: return null

        var updatedMessage: DbMessage? = null
        val updatedMessages = currentMessages.map { msg ->
            if (messageId == msg.id) {
                if (msg.content !is DbMessageContent.Text) return null // type must be Text

                val newText = DbMessageContent.Text(msg.content.text + newContent.text)
                msg.copy(content = newText).also {
                    updatedMessage = msg
                }
            } else {
                msg
            }
        }
        messages += (conversationId to updatedMessages)
        
        return updatedMessage // this will be null if no messageId was found
    }

    companion object {
        private var messages by mutableStateOf<Map<String, List<DbMessage>>>(emptyMap())
    }
}