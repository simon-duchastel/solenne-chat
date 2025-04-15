package org.duchastel.simon.solenne.di

import com.slack.circuit.foundation.Circuit
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.ui.Ui
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import org.duchastel.simon.solenne.screens.chat.ChatPresenter
import org.duchastel.simon.solenne.screens.chat.ChatScreen
import org.duchastel.simon.solenne.screens.chat.ChatUi

@DependencyGraph(AppScope::class)
@SingleIn(AppScope::class)
interface ApplicationGraph {
    val circuit: Circuit

    @Provides
    fun provideCircuit(): Circuit {
        return Circuit.Builder()
            .addPresenter<ChatScreen, ChatScreen.State> { _, _, _ ->
                ChatPresenter()
            }
            .addUi<ChatScreen, ChatScreen.State> { state, modifier ->
                ChatUi().Content(state, modifier)
            }
            .build()
    }
}