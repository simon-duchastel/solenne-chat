package com.duchastel.simon.solenne.fakes

import com.duchastel.simon.solenne.db.aiapikey.AIApiKeyDb
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeAiApiKeyDb(initialGeminiApiKey: String? = null) : AIApiKeyDb {

    private val geminiApiKeyFlow = MutableStateFlow(initialGeminiApiKey)

    override fun getGeminiApiKeyFlow(): Flow<String?> = geminiApiKeyFlow

    override suspend fun saveGeminiApiKey(apiKey: String): String {
        geminiApiKeyFlow.value = apiKey
        return apiKey
    }
}
