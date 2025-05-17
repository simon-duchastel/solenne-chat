package com.duchastel.simon.solenne.db.aimodelscope

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.coroutines.getStringOrNullFlow
import com.russhwolf.settings.set
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow

/**
 * Implementation of [AIApiKeyDb] that uses [Settings] to store AI model configurations.
 */
@OptIn(ExperimentalSettingsApi::class)
class AIApiKeyDbImpl @Inject constructor(
    @AIModelScopeSettings
    private val settings: ObservableSettings,
) : AIApiKeyDb {

    override suspend fun saveGeminiApiKey(apiKey: String): String {
        settings[KEY_GEMINI_API_KEY] = apiKey
        return apiKey
    }

    override fun getGeminiApiKeyFlow(): Flow<String?> {
        return settings.getStringOrNullFlow(KEY_GEMINI_API_KEY)
    }

    companion object {
        private const val KEY_GEMINI_API_KEY = "gemini_api_key"
    }
}