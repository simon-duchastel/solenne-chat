package com.duchastel.simon.solenne.data.chat

import kotlinx.coroutines.flow.Flow

/**
 * A repository for persisting and retrieving chat messages.
 *
 * Responsible for creating, storing, and retrieving all chat conversations.
 */
interface ChatMessageRepository {
    /**
     * Returns a cold [Flow] that emits the list of chat messages
     * in the conversation identified by [conversationId].
     *
     * @param conversationId the id of the conversation
     * @return a flow of the current list of [ChatMessage]s
     */
    fun getMessageFlowForConversation(conversationId: String): Flow<List<ChatMessage>>

    /**
     * Persists a new text message in the conversation, then
     * sends it to the AI backend and persists the AI's reply.
     *
     * @param conversationId the id of the conversation
     * @param text the plain‑text message from the user
     * @return the id of the new message
     */
    suspend fun addMessageToConversation(
        conversationId: String,
        author: MessageAuthor,
        text: String,
    ): String

    /**
     * Modifies an existing message in the conversation.
     */
    suspend fun modifyMessageFromConversation(
        conversationId: String,
        messageId: String,
        newText: String,
    )
}
