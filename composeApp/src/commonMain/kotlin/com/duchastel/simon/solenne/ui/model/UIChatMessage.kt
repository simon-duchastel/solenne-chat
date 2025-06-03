package com.duchastel.simon.solenne.ui.model

import com.duchastel.simon.solenne.data.chat.models.ChatMessage
import com.duchastel.simon.solenne.data.chat.models.MessageAuthor

/**
 * UI representation of a chat message for presentation logic.
 */
sealed interface UIChatMessage {
    data class TextMessage(
        val text: String,
        val isUser: Boolean,
        val id: String,
    ) : UIChatMessage

    data class ToolMessage(
        val toolName: String,
        val result: String? = null,
    ) : UIChatMessage
}

/**
 * Maps a domain ChatMessage to its UI counterpart.
 */
fun ChatMessage.toUIChatMessage(): UIChatMessage = when (this) {
    is ChatMessage.Text -> {
        UIChatMessage.TextMessage(
            id = this.id,
            text = this.text,
            isUser = this.author is MessageAuthor.User,
        )
    }
    is ChatMessage.ToolUse -> {
        UIChatMessage.ToolMessage(
            toolName = this.toolName,
            result = this.result?.text,
        )
    }
}
