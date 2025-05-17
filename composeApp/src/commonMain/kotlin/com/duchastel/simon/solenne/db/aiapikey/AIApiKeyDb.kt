package com.duchastel.simon.solenne.db.aiapikey

import kotlinx.coroutines.flow.Flow

/**
 * Interface for storing and retrieving AI model configurations in a persistent way.
 * Currently only supports Gemini model scope.
 */
interface AIApiKeyDb {
    /**
     * Returns a flow that emits the current Gemini API key,
     * or null if no API key has been saved.
     */
    fun getGeminiApiKeyFlow(): Flow<String?>

    /**
     * Saves the provided Gemini API key.
     */
    suspend fun saveGeminiApiKey(apiKey: String): String?
}