package com.duchastel.simon.solenne.tests.screens.settings

import com.duchastel.simon.solenne.fakes.FakeUrlOpener
import com.duchastel.simon.solenne.screens.mcplist.MCPListScreen
import com.duchastel.simon.solenne.screens.modelproviderselector.ModelProviderSelectorScreen
import com.duchastel.simon.solenne.screens.settings.SettingsPresenter
import com.duchastel.simon.solenne.screens.settings.SettingsScreen
import com.slack.circuit.test.FakeNavigator
import com.slack.circuit.test.test
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class SettingsPresenterTest {

    private lateinit var navigator: FakeNavigator
    private lateinit var urlOpener: FakeUrlOpener
    private lateinit var presenter: SettingsPresenter
    
    @BeforeTest
    fun setup() {
        val screen = SettingsScreen
        navigator = FakeNavigator(screen)
        urlOpener = FakeUrlOpener()
        presenter = SettingsPresenter(
            navigator = navigator,
            urlOpener = urlOpener
        )
    }
    
    @Test
    fun `present - back pressed event pops navigator`() = runTest {
        presenter.test {
            val state = expectMostRecentItem()
            val eventSink = state.eventSink
            
            eventSink(SettingsScreen.Event.BackPressed)
            
            // Verify navigator was popped
            navigator.awaitPop()
            
            cancelAndConsumeRemainingEvents()
        }
    }
    
    @Test
    fun `present - configure AI model pressed event navigates to ModelProviderSelectorScreen`() = runTest {
        presenter.test {
            val state = expectMostRecentItem()
            val eventSink = state.eventSink
            
            eventSink(SettingsScreen.Event.ConfigureAIModelPressed)
            
            assertEquals(ModelProviderSelectorScreen, navigator.awaitNextScreen())
            cancelAndConsumeRemainingEvents()
        }
    }
    
    @Test
    fun `present - configure MCP pressed event navigates to MCPListScreen`() = runTest {
        presenter.test {
            val state = expectMostRecentItem()
            val eventSink = state.eventSink
            
            eventSink(SettingsScreen.Event.ConfigureMcpPressed)
            
            assertEquals(MCPListScreen, navigator.awaitNextScreen())
            cancelAndConsumeRemainingEvents()
        }
    }
    
    @Test
    fun `present - view source pressed event launches GitHub URL`() = runTest {
        presenter.test {
            val state = expectMostRecentItem()
            val eventSink = state.eventSink
            
            eventSink(SettingsScreen.Event.ViewSourcePressed)
            
            assertEquals(
                expected = "https://github.com/simon-duchastel/solenne-chat",
                actual = urlOpener.lastLaunchedUrl,
            )
            cancelAndConsumeRemainingEvents()
        }
    }
}
