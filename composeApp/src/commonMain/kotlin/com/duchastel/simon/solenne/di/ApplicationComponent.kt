package com.duchastel.simon.solenne.di

import com.duchastel.simon.solenne.screens.chat.ChatPresenter
import com.slack.circuit.foundation.Circuit
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import com.duchastel.simon.solenne.screens.chat.ChatScreen
import com.duchastel.simon.solenne.screens.chat.ChatUi

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
                ChatUi(state, modifier)
            }
            .build()
    }
}