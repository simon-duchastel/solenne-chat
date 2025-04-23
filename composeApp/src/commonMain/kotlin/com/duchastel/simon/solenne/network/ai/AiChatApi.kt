package com.duchastel.simon.solenne.network.ai

import com.duchastel.simon.solenne.data.ai.AIModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

interface AiChatApi<S> where S : AIModelScope {
    /**
     * Sends a list of conversation messages to the AI and returns the plain text response,
     * streamed in a list of [GenerateContentResponse] objects as they are generated.
     * This supports multi-turn conversations with the AI.
     *
     * @param request The content of the conversation so far, which includes all
     * messages in the conversation and a new message from the user for the AI to respond to.
     * @return The AI's response as plain text
     */
    fun generateStreamingResponseForConversation(
        scope: S,
        request: GenerateContentRequest
    ): Flow<GenerateContentResponse>

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
        request: GenerateContentRequest,
    ): GenerateContentResponse
}

@Serializable
data class GenerateContentRequest(
    val contents: List<Content>,
    val tools: List<Tools>? = null
)

@Serializable
data class Content(
    val parts: List<Part>,
    val role: String
)

@Serializable
data class Part(
    val text: String? = null,
    @SerialName("functionCall") val functionCall: FunctionCall? = null,
    @SerialName("functionResponse") val functionResponse: FunctionResponse? = null,
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

@Serializable
data class Tools(
    val functionDeclarations: List<FunctionDeclaration>? = null
)

@Serializable
data class FunctionDeclaration(
    val name: String,
    val description: String,
    val parameters: JsonElement,
)

@Serializable
data class FunctionCall(
    val id: String? = null,
    val name: String,
    val arguments: Map<String, JsonElement?>?,
)

@Serializable
data class FunctionResponse(
    val id: String? = null,
    val name: String,
    val response: JsonElement,
)