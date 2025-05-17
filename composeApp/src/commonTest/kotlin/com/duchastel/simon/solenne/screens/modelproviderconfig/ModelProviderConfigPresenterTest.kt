package com.duchastel.simon.solenne.screens.modelproviderconfig

import com.duchastel.simon.solenne.data.ai.AIModelProvider
import com.duchastel.simon.solenne.data.ai.AIProviderConfig
import com.duchastel.simon.solenne.util.fakes.FakeAiChatRepository
import com.duchastel.simon.solenne.screens.conversationlist.ConversationListScreen
import com.slack.circuit.test.FakeNavigator
import com.slack.circuit.test.test
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ModelProviderConfigPresenterTest {

    private lateinit var navigator: FakeNavigator
    private lateinit var aiRepository: FakeAiChatRepository
    private lateinit var presenter: ModelProviderConfigPresenter

    @BeforeTest
    fun setup() {
        aiRepository = FakeAiChatRepository()
        navigator = FakeNavigator(ModelProviderConfigScreen(modelProvider = AIModelProvider.Gemini))
    }

    @Test
    fun `present - handles API key change event`() = runTest {
        val screen = ModelProviderConfigScreen(modelProvider = AIModelProvider.Gemini)
        presenter = ModelProviderConfigPresenter(
            navigator = navigator,
            screen = screen,
            aiChatRepository = aiRepository
        )

        presenter.test {
            val state = expectMostRecentItem()
            val eventSink = state.eventSink

            // Initial null state
            assertEquals(null, state.apiKey)

            // Send API key change event
            eventSink(ModelProviderConfigScreen.Event.ApiKeyChanged("test-api-key"))

            // Get updated state
            val newState = expectMostRecentItem()
            assertEquals("test-api-key", newState.apiKey)

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `present - handles save event for Gemini provider`() = runTest {
        val screen = ModelProviderConfigScreen(modelProvider = AIModelProvider.Gemini)
        presenter = ModelProviderConfigPresenter(
            navigator = navigator,
            screen = screen,
            aiChatRepository = aiRepository
        )

        presenter.test {
            val state = expectMostRecentItem()
            val eventSink = state.eventSink

            // Set API key first
            eventSink(ModelProviderConfigScreen.Event.ApiKeyChanged("gemini-key"))
            expectMostRecentItem()

            // Then save
            eventSink(ModelProviderConfigScreen.Event.SavePressed)

            // Verify the repository was called with correct config
            val configs = aiRepository.getReceivedConfigs()
            assertEquals(1, configs.size)
            assertTrue(configs[0] is AIProviderConfig.GeminiConfig)
            assertEquals("gemini-key", (configs[0] as AIProviderConfig.GeminiConfig).apiKey)

            // Verify navigation to conversation list
            assertEquals(ConversationListScreen, navigator.awaitNextScreen())

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `present - handles back pressed event`() = runTest {
        val screen = ModelProviderConfigScreen(modelProvider = AIModelProvider.Gemini)
        presenter = ModelProviderConfigPresenter(
            navigator = navigator,
            screen = screen,
            aiChatRepository = aiRepository
        )

        presenter.test {
            val state = expectMostRecentItem()
            val eventSink = state.eventSink

            eventSink(ModelProviderConfigScreen.Event.BackPressed)

            navigator.awaitPop()

            cancelAndConsumeRemainingEvents()
        }
    }
}