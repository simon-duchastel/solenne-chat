package com.duchastel.simon.solenne.screens.splash

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.duchastel.simon.solenne.data.ai.AiChatRepository
import com.duchastel.simon.solenne.screens.conversationlist.ConversationListScreen
import com.duchastel.simon.solenne.screens.modelproviderselector.ModelProviderSelectorScreen
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.Inject

class SplashPresenter @Inject constructor(
    @Assisted private val navigator: Navigator,
    private val aiChatRepository: AiChatRepository,
) : Presenter<SplashScreen.State> {

    @Composable
    override fun present(): SplashScreen.State {
        val availableModels by aiChatRepository.getAvailableModelsFlow()
            .collectAsState(initial = null)

        // Check if at least one model is configured
        LaunchedEffect(availableModels) {
            val anyModelIsAvailable = availableModels?.any { it.scope != null }
            if (anyModelIsAvailable != null) {
                if (anyModelIsAvailable) {
                    navigator.resetRoot(ConversationListScreen)
                } else {
                    navigator.resetRoot(ModelProviderSelectorScreen)
                }
            }
        }

        return SplashScreen.State()
    }

    @AssistedFactory
    fun interface Factory {
        fun create(navigator: Navigator): SplashPresenter
    }
}