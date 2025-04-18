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
     * @param conversationId the unique identifier of the conversation
     * @return a flow of the current list of [ChatMessage]s
     */
    fun getMessageFlowForConversation(conversationId: String): Flow<List<ChatMessage>>

    /**
     * Persists a new text message in the conversation, then
     * sends it to the AI backend and persists the AI's reply.
     *
     * @param conversationId the unique identifier of the conversation
     * @param text the plain‑text message from the user
     */
    suspend fun addMessageToConversation(
        conversationId: String,
        author: MessageAuthor,
        text: String,
    )
}
