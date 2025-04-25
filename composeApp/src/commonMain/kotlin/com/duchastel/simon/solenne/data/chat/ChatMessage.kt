package com.duchastel.simon.solenne.data.chat

/**
 * Represents a single message in the chat.
 */
data class ChatMessage(
    val text: String,
    val author: MessageAuthor,
    val id: String,
)

/**
 * Represents the author of a message.
 */
sealed class MessageAuthor {
    data object User: MessageAuthor()
    data object AI: MessageAuthor()
    data object System: MessageAuthor()
}