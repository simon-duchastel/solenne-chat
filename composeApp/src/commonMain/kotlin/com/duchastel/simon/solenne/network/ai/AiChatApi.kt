package com.duchastel.simon.solenne.network.ai

import com.duchastel.simon.solenne.data.ai.AIModelScope
import kotlinx.coroutines.flow.Flow

interface AiChatApi<S> where S : AIModelScope {
    /**
     * Sends a list of conversation messages to the AI and returns the plain text response,
     * streamed in a list of [GenerateContentResponse] objects as they are generated.
     * This supports multi-turn conversations with the AI.
     *
     * @param conversation The content of the conversation so far, which includes all
     * messages in the conversation and a new message from the user for the AI to respond to.
     *
     * @return The AI's response as plain text
     */
    fun generateStreamingResponseForConversation(
        scope: S,
        conversation: Conversation,
        systemPrompt: String? = null,
        tools: List<Tool> = emptyList(),
    ): Flow<ConversationResponse>

    /**
     * Sends a list of conversation messages to the AI and returns the plain text response.
     * This supports multi-turn conversations with the AI.
     *
     * @param request The content of the conversation so far, which includes all
     * messages in the conversation and a new message from the user for the AI to respond to.
     * @return The AI's response as plain text
     */
    suspend fun generateResponseForConversation(
        scope: S,
        conversation: Conversation,
        systemPrompt: String? = null,
        tools: List<Tool> = emptyList(),
    ): ConversationResponse
}
