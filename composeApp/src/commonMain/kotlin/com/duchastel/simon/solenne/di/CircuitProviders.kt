package com.duchastel.simon.solenne.di

import com.duchastel.simon.solenne.screens.addmcp.AddMCPPresenter
import com.duchastel.simon.solenne.screens.addmcp.AddMCPScreen
import com.duchastel.simon.solenne.screens.addmcp.AddMCPUi
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
import com.duchastel.simon.solenne.screens.settings.SettingsPresenter
import com.duchastel.simon.solenne.screens.settings.SettingsScreen
import com.duchastel.simon.solenne.screens.settings.SettingsUi
import com.duchastel.simon.solenne.screens.splash.SplashPresenter
import com.duchastel.simon.solenne.screens.splash.SplashScreen
import com.duchastel.simon.solenne.screens.splash.SplashUi
import com.duchastel.simon.solenne.screens.topbar.TopBarPresenter
import com.duchastel.simon.solenne.screens.topbar.TopBarScreen
import com.duchastel.simon.solenne.screens.topbar.TopBarUi
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
        addMCPPresenterFactory: AddMCPPresenter.Factory,
        settingsPresenterFactory: SettingsPresenter.Factory,
        splashPresenterFactory: SplashPresenter.Factory,
        topBarPresenterFactory: TopBarPresenter.Factory,
    ): Circuit {
        return Circuit.Builder()
            .addPresenter<SplashScreen, SplashScreen.State> { _, navigator, _ ->
                splashPresenterFactory.create(navigator)
            }
            .addUi<SplashScreen, SplashScreen.State> { state, modifier ->
                SplashUi(state, modifier)
            }
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
            .addPresenter<AddMCPScreen, AddMCPScreen.State> { _, navigator, _ ->
                addMCPPresenterFactory.create(navigator)
            }
            .addUi<AddMCPScreen, AddMCPScreen.State> { state, modifier ->
                AddMCPUi(state, modifier)
            }
            .addPresenter<SettingsScreen, SettingsScreen.State> { _, navigator, _ ->
                settingsPresenterFactory.create(navigator)
            }
            .addUi<SettingsScreen, SettingsScreen.State> { state, modifier ->
                SettingsUi(state, modifier)
            }
            .addPresenter<TopBarScreen, TopBarScreen.State> { screen, _, _ ->
                topBarPresenterFactory.create(screen)
            }
            .addUi<TopBarScreen, TopBarScreen.State> { state, modifier ->
                TopBarUi(state, modifier)
            }
            .build()
    }
}