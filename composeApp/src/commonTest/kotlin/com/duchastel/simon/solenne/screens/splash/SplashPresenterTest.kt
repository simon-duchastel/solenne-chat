package com.duchastel.simon.solenne.screens.splash

import com.duchastel.simon.solenne.data.ai.AIModelProviderStatus
import com.duchastel.simon.solenne.data.ai.AIModelScope
import com.duchastel.simon.solenne.screens.conversationlist.ConversationListScreen
import com.duchastel.simon.solenne.screens.modelproviderselector.ModelProviderSelectorScreen
import com.duchastel.simon.solenne.util.fakes.FakeAiChatRepository
import com.slack.circuit.test.FakeNavigator
import com.slack.circuit.test.test
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class SplashPresenterTest {
    private lateinit var navigator: FakeNavigator
    private lateinit var aiRepository: FakeAiChatRepository
    private lateinit var presenter: SplashPresenter

    @BeforeTest
    fun setup() {
        navigator = FakeNavigator(SplashScreen)
        aiRepository = FakeAiChatRepository()
        presenter = SplashPresenter(
            navigator = navigator,
            aiChatRepository = aiRepository,
        )
    }

    @Test
    fun `present - navigates to ModelProviderSelector when no models are configured`() = runTest {
        // Create a repository with unConfigured models
        val unConfiguredModels = listOf(AIModelProviderStatus.Gemini(null))
        val repo = FakeAiChatRepository(availableModels = unConfiguredModels)

        // Create presenter with this repository
        val testNavigator = FakeNavigator(SplashScreen)
        val testPresenter = SplashPresenter(
            navigator = testNavigator,
            aiChatRepository = repo,
        )

        testPresenter.test {
            awaitItem()
            assertEquals(ModelProviderSelectorScreen, testNavigator.awaitNextScreen())
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `present - navigates to ConversationList when model is configured`() = runTest {
        // Create a repository with configured model
        val mockScope = AIModelScope.GeminiModelScope("test-api-key")
        val configuredModels = listOf(AIModelProviderStatus.Gemini(mockScope))
        val repo = FakeAiChatRepository(availableModels = configuredModels)

        // Create presenter with this repository
        val testNavigator = FakeNavigator(SplashScreen)
        val testPresenter = SplashPresenter(
            navigator = testNavigator,
            aiChatRepository = repo,
        )

        testPresenter.test {
            awaitItem()
            assertEquals(ConversationListScreen, testNavigator.awaitNextScreen())
            cancelAndConsumeRemainingEvents()
        }
    }
}