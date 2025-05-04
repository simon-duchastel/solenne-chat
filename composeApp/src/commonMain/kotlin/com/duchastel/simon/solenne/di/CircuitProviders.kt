package com.duchastel.simon.solenne.di

import com.duchastel.simon.solenne.screens.chat.ChatPresenter
import com.duchastel.simon.solenne.screens.chat.ChatScreen
import com.duchastel.simon.solenne.screens.chat.ChatUi
import com.duchastel.simon.solenne.screens.conversationlist.ConversationListPresenter
import com.duchastel.simon.solenne.screens.conversationlist.ConversationListScreen
import com.duchastel.simon.solenne.screens.conversationlist.ConversationListUi
import com.duchastel.simon.solenne.screens.mcplist.MCPListPresenter
import com.duchastel.simon.solenne.screens.mcplist.MCPListScreen
import com.duchastel.simon.solenne.screens.mcplist.MCPListUi
import com.duchastel.simon.solenne.screens.modelproviderconfig.ModelProviderConfigPresenter
import com.duchastel.simon.solenne.screens.modelproviderconfig.ModelProviderConfigScreen
import com.duchastel.simon.solenne.screens.modelproviderconfig.ModelProviderConfigUi
import com.duchastel.simon.solenne.screens.modelproviderselector.ModelProviderSelectorPresenter
import com.duchastel.simon.solenne.screens.modelproviderselector.ModelProviderSelectorScreen
import com.duchastel.simon.solenne.screens.modelproviderselector.ModelProviderSelectorUi
import com.slack.circuit.foundation.Circuit
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

interface CircuitProviders {
    val circuit: Circuit

    @SingleIn(AppScope::class)
    @Provides
    fun provideCircuit(
        chatPresenterFactory: ChatPresenter.Factory,
        conversationListPresenterFactory: ConversationListPresenter.Factory,
        modelProviderSelectorPresenterFactory: ModelProviderSelectorPresenter.Factory,
        modelProviderConfigPresenterFactory: ModelProviderConfigPresenter.Factory,
        mcpListPresenterFactory: MCPListPresenter.Factory,
    ): Circuit {
        return Circuit.Builder()
            .addPresenter<ChatScreen, ChatScreen.State> { screen, navigator, _ ->
                chatPresenterFactory.create(screen, navigator)
            }
            .addUi<ChatScreen, ChatScreen.State> { state, modifier ->
                ChatUi(state, modifier)
            }
            .addPresenter<ConversationListScreen, ConversationListScreen.State> { _, navigator, _ ->
                conversationListPresenterFactory.create(navigator)
            }
            .addUi<ConversationListScreen, ConversationListScreen.State> { state, modifier ->
                ConversationListUi(state, modifier)
            }
            .addPresenter<ModelProviderSelectorScreen, ModelProviderSelectorScreen.State> { _, navigator, _ ->
                modelProviderSelectorPresenterFactory.create(navigator)
            }
            .addUi<ModelProviderSelectorScreen, ModelProviderSelectorScreen.State> { state, modifier ->
                ModelProviderSelectorUi(state, modifier)
            }
            .addPresenter<ModelProviderConfigScreen, ModelProviderConfigScreen.State> { screen, navigator, _ ->
                modelProviderConfigPresenterFactory.create(screen, navigator)
            }
            .addUi<ModelProviderConfigScreen, ModelProviderConfigScreen.State> { state, modifier ->
                ModelProviderConfigUi(state, modifier)
            }
            .addPresenter<MCPListScreen, MCPListScreen.State> { _, navigator, _ ->
                mcpListPresenterFactory.create(navigator)
            }
            .addUi<MCPListScreen, MCPListScreen.State> { state, modifier ->
                MCPListUi(state, modifier)
            }
            .build()
    }
}