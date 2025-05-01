package com.duchastel.simon.solenne.data.chat.models

import kotlinx.serialization.json.JsonElement

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

    data class ToolUse(
        override val id: String,
        val toolName: String,
        val argumentsSupplied: Map<String, JsonElement>,
        val result: ToolResult? = null,
    ): ChatMessage {
        override val author: MessageAuthor = MessageAuthor.AI

        data class ToolResult(
            val text: String,
            val isError: Boolean,
        )
    }
}

/**
 * Represents the author of a message.
 */
sealed class MessageAuthor {
    data object User: MessageAuthor()
    data object AI: MessageAuthor()
}