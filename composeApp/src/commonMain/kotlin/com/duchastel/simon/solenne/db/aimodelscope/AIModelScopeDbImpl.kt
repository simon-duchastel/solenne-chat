package com.duchastel.simon.solenne.db.aimodelscope

import com.duchastel.simon.solenne.data.ai.AIModelScope.GeminiModelScope
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.Settings
import com.russhwolf.settings.coroutines.getStringOrNullFlow
import com.russhwolf.settings.set
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Implementation of [AIModelScopeDb] that uses [Settings] to store AI model configurations.
 */
@OptIn(ExperimentalSettingsApi::class)
class AIModelScopeDbImpl @Inject constructor(
    @AIModelScopeSettings
    private val settings: ObservableSettings,
) : AIModelScopeDb {

    override suspend fun saveGeminiApiKey(apiKey: String) {
        settings[KEY_GEMINI_API_KEY] = apiKey
    }

    override fun getGeminiModelScopeFlow(): Flow<GeminiModelScope?> {
        return settings.getStringOrNullFlow(KEY_GEMINI_API_KEY)
            .map { apiKey ->
                if (apiKey == null) {
                    null
                } else {
                    GeminiModelScope(apiKey = apiKey)
                }
            }
    }

    companion object {
        private const val KEY_GEMINI_API_KEY = "gemini_api_key"
    }
}