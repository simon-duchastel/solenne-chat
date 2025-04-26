package com.duchastel.simon.solenne.data.chat

/**
 * Represents a single message in the chat.
 */
sealed interface ChatMessage {
    val id: String
    val author: MessageAuthor

    data class Text(
        override val id: String,
        override val author: MessageAuthor,
        val text: String,
    ): ChatMessage

    data class ToolRequest(
        override val id: String,
        override val author: MessageAuthor,
    ): ChatMessage

    data class ToolUse(
        override val id: String,
        override val author: MessageAuthor,
    ): ChatMessage
}

/**
 * Represents the author of a message.
 */
sealed class MessageAuthor {
    data object User: MessageAuthor()
    data object AI: MessageAuthor()
}