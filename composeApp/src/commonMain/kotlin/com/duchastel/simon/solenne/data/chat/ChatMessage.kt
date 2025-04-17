package com.duchastel.simon.solenne.data.chat

data class ChatMessage(
    val text: String,
    val author: MessageAuthor,
    val id: String,
)

sealed class MessageAuthor {
    data object User: MessageAuthor()
    data object AI: MessageAuthor()
}