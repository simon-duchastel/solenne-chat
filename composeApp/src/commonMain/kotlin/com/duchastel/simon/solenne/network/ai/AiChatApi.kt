package com.duchastel.simon.solenne.network.ai

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

interface AiChatApi {
    /**
     * Sends a list of conversation messages to the AI and returns the plain text response.
     * This supports multi-turn conversations with the AI.
     *
     * @param request The content of the conversation so far, which includes all
     * messages in the conversation and a new message from the user for the AI to respond to.
     * @return The AI's response as plain text
     */
    suspend fun generateResponseForConversation(request: GenerateContentRequest): GenerateContentResponse
}

@Serializable
data class GenerateContentRequest(
    val contents: List<Content>
)

@Serializable
data class Content(
    val parts: List<Part>,
    val role: String
)

@Serializable
data class Part(
    val text: String
)

@Serializable
data class GenerateContentResponse(
    val candidates: List<Candidate> = emptyList()
)

@Serializable
data class Candidate(
    val content: Content,
    @SerialName("finishReason") val finishReason: String? = null,
)