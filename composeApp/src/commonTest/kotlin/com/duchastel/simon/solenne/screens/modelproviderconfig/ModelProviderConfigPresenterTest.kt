package com.duchastel.simon.solenne.screens.modelproviderconfig

import com.duchastel.simon.solenne.data.ai.AIModelProvider
import com.duchastel.simon.solenne.data.ai.AIProviderConfig
import com.duchastel.simon.solenne.fakes.FakeAiChatRepository
import com.duchastel.simon.solenne.screens.conversationlist.ConversationListScreen
import com.duchastel.simon.solenne.screens.modelproviderselector.UiModelProvider
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
        navigator = FakeNavigator(ModelProviderConfigScreen(modelProvider = AIModelProvider.OpenAI))
    }

    @Test
    fun `present - maps model provider to UI model correctly`() = runTest {
        val screen = ModelProviderConfigScreen(modelProvider = AIModelProvider.OpenAI)
        presenter = ModelProviderConfigPresenter(
            navigator = navigator,
            screen = screen,
            aiChatRepository = aiRepository
        )

        presenter.test {
            val state = expectMostRecentItem()
            assertEquals(UiModelProvider.OpenAI, state.modelProvider)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `present - handles API key change event`() = runTest {
        val screen = ModelProviderConfigScreen(modelProvider = AIModelProvider.OpenAI)
        presenter = ModelProviderConfigPresenter(
            navigator = navigator,
            screen = screen,
            aiChatRepository = aiRepository
        )

        presenter.test {
            var state = expectMostRecentItem()
            val eventSink = state.eventSink

            // Initial null state
            assertEquals(null, state.apiKey)

            // Send API key change event
            eventSink(ModelProviderConfigScreen.Event.ApiKeyChanged("test-api-key"))

            // Get updated state
            state = expectMostRecentItem()
            assertEquals("test-api-key", state.apiKey)

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `present - handles save event for OpenAI provider`() = runTest {
        val screen = ModelProviderConfigScreen(modelProvider = AIModelProvider.OpenAI)
        presenter = ModelProviderConfigPresenter(
            navigator = navigator,
            screen = screen,
            aiChatRepository = aiRepository
        )

        presenter.test {
            var state = expectMostRecentItem()
            val eventSink = state.eventSink

            // Set API key first
            eventSink(ModelProviderConfigScreen.Event.ApiKeyChanged("openai-key"))
            state = expectMostRecentItem()

            // Then save
            eventSink(ModelProviderConfigScreen.Event.SavePressed)

            // Verify the repository was called with correct config
            val configs = aiRepository.getReceivedConfigs()
            assertEquals(1, configs.size)
            assertTrue(configs[0] is AIProviderConfig.OpenAIConfig)
            assertEquals("openai-key", (configs[0] as AIProviderConfig.OpenAIConfig).apiKey)

            // Verify navigation to conversation list
            assertEquals(ConversationListScreen, navigator.awaitNextScreen())

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `present - handles save event for Anthropic provider`() = runTest {
        val screen = ModelProviderConfigScreen(modelProvider = AIModelProvider.Anthropic)
        presenter = ModelProviderConfigPresenter(
            navigator = navigator,
            screen = screen,
            aiChatRepository = aiRepository
        )

        presenter.test {
            var state = expectMostRecentItem()
            val eventSink = state.eventSink

            // Set API key first
            eventSink(ModelProviderConfigScreen.Event.ApiKeyChanged("anthropic-key"))
            state = expectMostRecentItem()

            // Then save
            eventSink(ModelProviderConfigScreen.Event.SavePressed)

            // Verify the repository was called with correct config
            val configs = aiRepository.getReceivedConfigs()
            assertEquals(1, configs.size)
            assertTrue(configs[0] is AIProviderConfig.AnthropicConfig)
            assertEquals("anthropic-key", (configs[0] as AIProviderConfig.AnthropicConfig).apiKey)

            // Verify navigation to conversation list
            assertEquals(ConversationListScreen, navigator.awaitNextScreen())

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `present - handles save event for DeepSeek provider`() = runTest {
        val screen = ModelProviderConfigScreen(modelProvider = AIModelProvider.DeepSeek)
        presenter = ModelProviderConfigPresenter(
            navigator = navigator,
            screen = screen,
            aiChatRepository = aiRepository
        )

        presenter.test {
            var state = expectMostRecentItem()
            val eventSink = state.eventSink

            // Set API key first
            eventSink(ModelProviderConfigScreen.Event.ApiKeyChanged("deepseek-key"))
            state = expectMostRecentItem()

            // Then save
            eventSink(ModelProviderConfigScreen.Event.SavePressed)

            // Verify the repository was called with correct config
            val configs = aiRepository.getReceivedConfigs()
            assertEquals(1, configs.size)
            assertTrue(configs[0] is AIProviderConfig.DeepSeekConfig)
            assertEquals("deepseek-key", (configs[0] as AIProviderConfig.DeepSeekConfig).apiKey)

            // Verify navigation to conversation list
            assertEquals(ConversationListScreen, navigator.awaitNextScreen())

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
            var state = expectMostRecentItem()
            val eventSink = state.eventSink

            // Set API key first
            eventSink(ModelProviderConfigScreen.Event.ApiKeyChanged("gemini-key"))
            state = expectMostRecentItem()

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
    fun `present - handles save event for Grok provider`() = runTest {
        val screen = ModelProviderConfigScreen(modelProvider = AIModelProvider.Grok)
        presenter = ModelProviderConfigPresenter(
            navigator = navigator,
            screen = screen,
            aiChatRepository = aiRepository
        )

        presenter.test {
            var state = expectMostRecentItem()
            val eventSink = state.eventSink

            // Set API key first
            eventSink(ModelProviderConfigScreen.Event.ApiKeyChanged("grok-key"))
            state = expectMostRecentItem()

            // Then save
            eventSink(ModelProviderConfigScreen.Event.SavePressed)

            // Verify the repository was called with correct config
            val configs = aiRepository.getReceivedConfigs()
            assertEquals(1, configs.size)
            assertTrue(configs[0] is AIProviderConfig.GrokConfig)
            assertEquals("grok-key", (configs[0] as AIProviderConfig.GrokConfig).apiKey)

            // Verify navigation to conversation list
            assertEquals(ConversationListScreen, navigator.awaitNextScreen())

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `present - handles back pressed event`() = runTest {
        val screen = ModelProviderConfigScreen(modelProvider = AIModelProvider.OpenAI)
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