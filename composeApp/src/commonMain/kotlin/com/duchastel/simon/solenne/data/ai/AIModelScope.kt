package com.duchastel.simon.solenne.data.ai

sealed interface AIModelScope {
    class GeminiModelScope internal constructor(
        internal val apiKey: String,
    ) : AIModelScope
}

sealed interface AIModelProvider {
    val scope: AIModelScope? // a scope if it's configured, null otherwise
    val availableModels: List<AIModel>

    data class Gemini(
        override val scope: AIModelScope?,
    ) : AIModelProvider {
        override val availableModels: List<AIModel> = listOf(
            AIModel("gemini-2.0-flash"),
        )
    }

    data class OpenAI(
        override val scope: AIModelScope?
    ) : AIModelProvider {
        override val availableModels: List<AIModel> = listOf()
    }

    data class Anthropic(
        override val scope: AIModelScope?
    ) : AIModelProvider {
        override val availableModels: List<AIModel> = listOf()
    }

    data class DeepSeek(
        override val scope: AIModelScope?
    ) : AIModelProvider {
        override val availableModels: List<AIModel> = listOf()
    }

    data class Grok(
        override val scope: AIModelScope?
    ) : AIModelProvider {
        override val availableModels: List<AIModel> = listOf()
    }
}

data class AIModel(
    val name: String,
)