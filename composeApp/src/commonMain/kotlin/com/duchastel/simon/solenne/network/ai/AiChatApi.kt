package com.duchastel.simon.solenne.network.ai

interface AiChatApi {
    /**
     * Sends the [prompt] to the AI and returns the plain text response.
     */
    suspend fun generateContent(prompt: String): String
}