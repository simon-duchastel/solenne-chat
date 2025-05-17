package com.duchastel.simon.solenne.data.ai

import com.duchastel.simon.solenne.parcel.Parcelable
import com.duchastel.simon.solenne.parcel.Parcelize

sealed interface AIModelScope {
    class GeminiModelScope internal constructor(
        internal val apiKey: String,
    ) : AIModelScope
}

@Parcelize
sealed interface AIModelProvider: Parcelable {
    data object Gemini : AIModelProvider
}

sealed interface AIModelProviderStatus<T: AIModelProvider> {
    val scope: AIModelScope? // a scope if it's configured, null otherwise
    val availableModels: List<AIModel>

    data class Gemini(
        override val scope: AIModelScope?,
    ) : AIModelProviderStatus<AIModelProvider.Gemini> {
        override val availableModels: List<AIModel> = listOf(
            AIModel("gemini-2.0-flash"),
        )
    }
}

data class AIModel(
    val name: String,
)

sealed class AIProviderConfig<T : AIModelProvider> {
    data class GeminiConfig(
        val apiKey: String,
    ) : AIProviderConfig<AIModelProvider.Gemini>()
}