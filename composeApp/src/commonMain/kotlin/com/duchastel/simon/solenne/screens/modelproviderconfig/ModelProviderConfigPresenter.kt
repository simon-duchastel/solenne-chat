package com.duchastel.simon.solenne.screens.modelproviderconfig

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.duchastel.simon.solenne.data.ai.AIModelProvider
import com.duchastel.simon.solenne.data.ai.AIModelProvider.Anthropic
import com.duchastel.simon.solenne.data.ai.AIModelProvider.DeepSeek
import com.duchastel.simon.solenne.data.ai.AIModelProvider.Gemini
import com.duchastel.simon.solenne.data.ai.AIModelProvider.Grok
import com.duchastel.simon.solenne.data.ai.AIModelProvider.OpenAI
import com.duchastel.simon.solenne.data.ai.AIProviderConfig
import com.duchastel.simon.solenne.data.ai.AiChatRepository
import com.duchastel.simon.solenne.screens.conversationlist.ConversationListScreen
import com.duchastel.simon.solenne.screens.modelproviderconfig.ModelProviderConfigScreen.Event
import com.duchastel.simon.solenne.screens.modelproviderselector.UiModelProvider
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.Inject

class ModelProviderConfigPresenter @Inject constructor(
    @Assisted private val navigator: Navigator,
    @Assisted private val screen: ModelProviderConfigScreen,
    private val aiChatRepository: AiChatRepository,
) : Presenter<ModelProviderConfigScreen.State> {

    @Composable
    override fun present(): ModelProviderConfigScreen.State {
        var apiKey: String? by remember { mutableStateOf(null) }

        return ModelProviderConfigScreen.State(
            apiKey = apiKey,
            modelProvider = screen.modelProvider.toUiModel()
        ) { event ->
            when (event) {
                is Event.ApiKeyChanged -> {
                    apiKey = event.apiKey
                }
                is Event.SavePressed -> {
                    when (screen.modelProvider) {
                        is OpenAI -> {
                            aiChatRepository.configureModel(AIProviderConfig.OpenAIConfig(apiKey!!))
                        }
                        is Anthropic -> {
                            aiChatRepository.configureModel(AIProviderConfig.AnthropicConfig(apiKey!!))
                        }
                        is DeepSeek -> {
                            aiChatRepository.configureModel(AIProviderConfig.DeepSeekConfig(apiKey!!))
                        }
                        is Gemini -> {
                            aiChatRepository.configureModel(AIProviderConfig.GeminiConfig(apiKey!!))
                        }
                        is Grok -> {
                            aiChatRepository.configureModel(AIProviderConfig.GrokConfig(apiKey!!))
                        }
                    }
                    navigator.goTo(ConversationListScreen)
                }
                is Event.BackPressed -> {
                    navigator.pop()
                }
            }
        }
    }

    @AssistedFactory
    fun interface Factory {
        fun create(
            screen: ModelProviderConfigScreen,
            navigator: Navigator,
        ): ModelProviderConfigPresenter
    }
}

fun AIModelProvider.toUiModel(): UiModelProvider {
    return when (this) {
        is OpenAI -> UiModelProvider.OpenAI
        is Anthropic -> UiModelProvider.Anthropic
        is DeepSeek -> UiModelProvider.DeepSeek
        is Gemini -> UiModelProvider.Gemini
        is Grok -> UiModelProvider.Grok
    }
}