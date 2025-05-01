package com.duchastel.simon.solenne.data.ai

import kotlinx.coroutines.flow.Flow

/**
 * A repository for chatting with the AI backend.
 *
 * Responsible for managing all AI chats, including which models are available
 * and chats with the particular AI models.
 */
interface AiChatRepository {

    /**
     * Gets a list of available AI models.
     */
    fun getAvailableModelsFlow(): Flow<List<AIModelProvider>>

    /**
     * Sends a text message as the user to a conversation.
     */
    suspend fun sendTextMessageFromUserToConversation(
        aiModelScope: AIModelScope,
        conversationId: String,
        text: String,
    )
}