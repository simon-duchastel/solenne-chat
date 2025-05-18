package com.duchastel.simon.solenne.screens.settings

import androidx.compose.runtime.Composable
import com.duchastel.simon.solenne.screens.addmcp.AddMCPScreen
import com.duchastel.simon.solenne.screens.modelproviderselector.ModelProviderSelectorScreen
import com.duchastel.simon.solenne.screens.settings.SettingsScreen.Event
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.Inject

class SettingsPresenter @Inject constructor(
    @Assisted private val navigator: Navigator,
) : Presenter<SettingsScreen.State> {

    @Composable
    override fun present(): SettingsScreen.State {
        return SettingsScreen.State { event ->
            when (event) {
                is Event.BackPressed -> {
                    navigator.pop()
                }

                is Event.ModelProviderSelectorPressed -> {
                    navigator.goTo(ModelProviderSelectorScreen)
                }

                is Event.AddMCPPressed -> {
                    navigator.goTo(AddMCPScreen)
                }
            }
        }
    }

    @AssistedFactory
    fun interface Factory {
        fun create(
            navigator: Navigator,
        ): SettingsPresenter
    }
}