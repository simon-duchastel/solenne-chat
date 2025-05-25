package com.duchastel.simon.solenne.ui.model

import com.duchastel.simon.solenne.data.chat.models.ChatMessage
import com.duchastel.simon.solenne.data.chat.models.MessageAuthor

/**
 * UI representation of a chat message for presentation logic.
 */
data class UIChatMessage(
    val text: String,
    val isUser: Boolean,
    val id: String,
    val toolInfo: ToolInfo? = null,
) {
    data class ToolInfo(
        val toolName: String,
        val result: String? = null,
    )
}

/**
 * Maps a domain ChatMessage to its UI counterpart.
 */
fun ChatMessage.toUIChatMessage(): UIChatMessage = when (this) {
    is ChatMessage.Text -> {
        UIChatMessage(
            id = this.id,
            text = this.text,
            isUser = this.author is MessageAuthor.User,
        )
    }
    is ChatMessage.ToolUse -> {
        UIChatMessage(
            id = this.id,
            text = "Using tool ${this.toolName}..."
                    + if (this.result != null) "\nResult: ${this.result}" else "",
            isUser = this.author is MessageAuthor.User,
        )
    }
}
