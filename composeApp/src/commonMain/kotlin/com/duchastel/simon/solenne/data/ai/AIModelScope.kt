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
    data object OpenAI : AIModelProvider
    data object Anthropic : AIModelProvider
    data object DeepSeek : AIModelProvider
    data object Grok : AIModelProvider
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

    data class OpenAI(
        override val scope: AIModelScope?
    ) : AIModelProviderStatus<AIModelProvider.OpenAI> {
        override val availableModels: List<AIModel> = listOf()
    }

    data class Anthropic(
        override val scope: AIModelScope?
    ) : AIModelProviderStatus<AIModelProvider.Anthropic> {
        override val availableModels: List<AIModel> = listOf()
    }

    data class DeepSeek(
        override val scope: AIModelScope?
    ) : AIModelProviderStatus<AIModelProvider.DeepSeek> {
        override val availableModels: List<AIModel> = listOf()
    }

    data class Grok(
        override val scope: AIModelScope?
    ) : AIModelProviderStatus<AIModelProvider.Grok> {
        override val availableModels: List<AIModel> = listOf()
    }
}

data class AIModel(
    val name: String,
)

sealed class AIProviderConfig<T : AIModelProvider> {
    data class GeminiConfig(
        val apiKey: String,
    ) : AIProviderConfig<AIModelProvider.Gemini>()

    data class OpenAIConfig(
        val apiKey: String,
    ) : AIProviderConfig<AIModelProvider.OpenAI>()

    data class AnthropicConfig(
        val apiKey: String,
    ) : AIProviderConfig<AIModelProvider.Anthropic>()

    data class DeepSeekConfig(
        val apiKey: String,
    ) : AIProviderConfig<AIModelProvider.DeepSeek>()

    data class GrokConfig(
        val apiKey: String,
    ) : AIProviderConfig<AIModelProvider.Grok>()
}