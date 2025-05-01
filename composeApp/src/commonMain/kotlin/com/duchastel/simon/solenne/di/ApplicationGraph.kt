package com.duchastel.simon.solenne.di

import com.duchastel.simon.solenne.screens.chat.ChatPresenter
import com.slack.circuit.foundation.Circuit
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import com.duchastel.simon.solenne.screens.chat.ChatScreen
import com.duchastel.simon.solenne.screens.chat.ChatUi

interface ApplicationGraph: DataProviders, DbProviders, NetworkProviders {
    val circuit: Circuit

    @SingleIn(AppScope::class)
    @Provides
    fun provideCircuit(
        chatPresenterFactory: ChatPresenter.Factory
    ): Circuit {
        return Circuit.Builder()
            .addPresenter<ChatScreen, ChatScreen.State> { screen, _, _ ->
                chatPresenterFactory.create(screen)
            }
            .addUi<ChatScreen, ChatScreen.State> { state, modifier ->
                ChatUi(state, modifier)
            }
            .build()
    }
}