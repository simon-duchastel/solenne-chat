package com.duchastel.simon.solenne.data

// ConversationId is the parent for messages but not a property per-message; associating is contextually done.
data class ChatMessage(
    val text: String,
    val author: MessageAuthor,
    val id: String,
)

sealed class MessageAuthor {
    data object User: MessageAuthor()
    data object AI: MessageAuthor()
}