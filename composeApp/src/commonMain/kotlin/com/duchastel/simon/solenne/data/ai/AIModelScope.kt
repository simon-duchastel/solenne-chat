package com.duchastel.simon.solenne.data.ai

sealed interface AIModelScope {
    class GeminiModelScope internal constructor(
        internal val apiKey: String,
    ) : AIModelScope
}

sealed interface AIModel {
    data object Gemini : AIModel
}