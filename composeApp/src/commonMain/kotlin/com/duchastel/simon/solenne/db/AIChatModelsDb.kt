package com.duchastel.simon.solenne.db

import com.duchastel.simon.solenne.data.ai.AIModelScope.GeminiModelScope
import kotlinx.coroutines.flow.Flow

/**
 * Interface for storing and retrieving AI model configurations in a persistent way.
 * Currently only supports Gemini model scope.
 */
interface AIChatModelsDb {
    /**
     * Saves the provided Gemini API key.
     */
    suspend fun saveGeminiApiKey(apiKey: String)

    /**
     * Returns a flow that emits the current Gemini model scope,
     * or null if no API key has been saved.
     */
    fun getGeminiModelScopeFlow(): Flow<GeminiModelScope?>
}