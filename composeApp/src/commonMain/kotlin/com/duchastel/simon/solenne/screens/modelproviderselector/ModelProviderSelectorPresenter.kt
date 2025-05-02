package com.duchastel.simon.solenne.screens.modelproviderselector

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.duchastel.simon.solenne.data.ai.AIModelProvider
import com.duchastel.simon.solenne.data.ai.AiChatRepository
import com.duchastel.simon.solenne.screens.modelproviderselector.ModelProviderSelectorScreen.Event
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.Inject
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.distinctUntilChanged

class ModelProviderSelectorPresenter @Inject constructor(
    @Assisted private val navigator: Navigator,
    private val aiChatRepository: AiChatRepository,
) : Presenter<ModelProviderSelectorScreen.State> {

    @Composable
    override fun present(): ModelProviderSelectorScreen.State {
        val providers by remember {
            aiChatRepository.getAvailableModelsFlow()
                .distinctUntilChanged()
        }.collectAsState(persistentListOf())

        val uiModels = providers
            .map(AIModelProvider::toUiModel)
            .plus(UiModelProvider.Other(name = null))
            .toPersistentList()
        return ModelProviderSelectorScreen.State(
            models = uiModels,
        ) { event ->
            when (event) {
                is Event.BackPressed -> {
                    navigator.pop()
                }
                is Event.ModelSelected -> {
                    // TODO - navigate to next screen
                }
            }
        }
    }

    @AssistedFactory
    fun interface Factory {
        fun create(navigator: Navigator): ModelProviderSelectorPresenter
    }
}

private fun AIModelProvider.toUiModel(): UiModelProvider {
    return when (this) {
        is AIModelProvider.OpenAI -> UiModelProvider.OpenAI
        is AIModelProvider.Anthropic -> UiModelProvider.Anthropic
        is AIModelProvider.DeepSeek -> UiModelProvider.DeepSeek
        is AIModelProvider.Gemini -> UiModelProvider.Gemini
        is AIModelProvider.Grok -> UiModelProvider.Grok
    }
}