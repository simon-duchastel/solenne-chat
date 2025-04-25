package com.duchastel.simon.solenne.data.ai

import com.duchastel.simon.solenne.data.chat.ChatMessage
import kotlinx.coroutines.flow.Flow

/**
 * A repository for chatting with the AI backend.
 *
 * Responsible for managing all AI chats, including which models are available
 * and chats with the particular AI models.
 */
interface AiChatRepository {
    /**
     * Gets all messages for a given conversation.
     */
    fun messageFlowForConversation(
        conversationId: String,
    ): Flow<List<ChatMessage>>

    /**
     * Sends a text message as the user to a conversation.
     */
    suspend fun sendTextMessageFromUserToConversation(
        aiModelScope: AIModelScope,
        conversationId: String,
        text: String,
    )
}