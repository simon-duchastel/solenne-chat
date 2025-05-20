package com.duchastel.simon.solenne.screens.topbar

import androidx.compose.runtime.Composable
import com.duchastel.simon.solenne.screens.topbar.TopBarScreen.Event
import com.duchastel.simon.solenne.util.circuit.LocalNavigator
import com.slack.circuit.runtime.presenter.Presenter
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.Inject

class TopBarPresenter @Inject constructor(
    @Assisted private val screen: TopBarScreen
) : Presenter<TopBarScreen.State> {

    @Composable
    override fun present(): TopBarScreen.State {
        // since TopBar isn't created within the same
        // NavigableCircuit instance, use a composition local navigator
        // to navigate within the rest of the hierarchy
        val navigator = LocalNavigator.current

        val itemsInBackStack = navigator?.peekBackStack()?.size ?: 0
        return TopBarScreen.State(
            title = screen.title,
            showBackButton = itemsInBackStack > 1, // backstack includes current screen
            eventSink = { event ->
                when (event) {
                    is Event.BackPressed -> {
                        navigator?.pop()
                    }
                }
            }
        )
    }

    @AssistedFactory
    fun interface Factory {
        fun create(
            screen: TopBarScreen
        ): TopBarPresenter
    }
}