package com.duchastel.simon.solenne.network.ai

import com.duchastel.simon.solenne.data.ai.AIModelScope
import com.duchastel.simon.solenne.util.types.Failure
import com.duchastel.simon.solenne.util.types.SolenneResult
import kotlinx.coroutines.flow.Flow

/**
 * Interface for AI chat generation APIs that support various AI models.
 * Each AI Model is defined by an [AIModelScope], which is used under-the-hood
 * to communicate with the model.
 * Provides methods to generate AI responses for conversations, with support for
 * both streaming and non-streaming responses, system prompts, and tools.
 */
interface AiChatApi<S> where S : AIModelScope {
    /**
     * Sends a conversation to the AI and returns the response as a stream.
     * This supports multi-turn conversations with the AI.
     *
     * @param scope The AI model scope to use for generation
     * @param conversation The content of the conversation so far
     * @param systemPrompt Optional system prompt to guide the AI's behavior
     * @param tools Optional list of tools the AI can use in its response
     *
     * @return A Flow of [ConversationResponse] objects wrapped in a [SolenneResult]
     * as they are generated. If an error occurs, a [Failure] will be emitted and then
     * no more items will be emitted.
     */
    fun generateStreamingResponseForConversation(
        scope: S,
        conversation: Conversation,
        systemPrompt: String? = null,
        tools: List<Tool> = emptyList(),
    ): Flow<SolenneResult<ConversationResponse>>

    /**
     * Sends a conversation to the AI and returns the complete response.
     * This supports multi-turn conversations with the AI.
     *
     * @param scope The AI model scope to use for generation
     * @param conversation The content of the conversation so far
     * @param systemPrompt Optional system prompt to guide the AI's behavior
     * @param tools Optional list of tools the AI can use in its response
     *
     * @return The AI's complete response as a [ConversationResponse] wrapped
     * in a [SolenneResult], with a [Failure] if there was an error.
     */
    suspend fun generateResponseForConversation(
        scope: S,
        conversation: Conversation,
        systemPrompt: String? = null,
        tools: List<Tool> = emptyList(),
    ): SolenneResult<ConversationResponse>
}