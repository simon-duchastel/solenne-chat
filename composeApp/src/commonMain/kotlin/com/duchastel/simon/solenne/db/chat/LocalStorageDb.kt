package com.duchastel.simon.solenne.db.chat

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import com.russhwolf.settings.ObservableSettings
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

class LocalStorageDb @Inject constructor(
    private val settings: ObservableSettings,
): ChatMessageDb {
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

    override suspend fun updateMessageContent(
        messageId: String,
        conversationId: String,
        newContent: DbMessageContent,
    ): DbMessage? {
        val currentMessages = messages[conversationId] ?: return null

        var updatedMessage: DbMessage? = null
        val updatedMessages = currentMessages.map { msg ->
            if (messageId == msg.id) {
                msg.updateMessageContent(newContent)?.also {
                    updatedMessage = it
                } ?: return null
            } else {
                msg
            }
        }
        messages += (conversationId to updatedMessages)

        return updatedMessage // this will be null if no messageId was found
    }

    /**
     * Helper function to update the content of a message.
     *
     */
    private fun DbMessage.updateMessageContent(
        newContent: DbMessageContent
    ): DbMessage? {
        when (this.content) {
            is DbMessageContent.Text -> {
                if (newContent !is DbMessageContent.Text) {
                    return null
                }
                return this.copy(content = newContent)
            }
            is DbMessageContent.ToolUse -> {
                if (newContent !is DbMessageContent.ToolUse) {
                    return null
                }
                return this.copy(content = newContent)
            }
        }
    }

    companion object {
        private var messages by mutableStateOf<Map<String, List<DbMessage>>>(emptyMap())
    }
}