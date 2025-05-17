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
        presenter = setupPresenter()
    }

    private fun setupPresenter(
        availableModels: List<AIModelProviderStatus<*>> = emptyList(),
    ): SplashPresenter {
        aiRepository = FakeAiChatRepository(availableModels = availableModels)
        return SplashPresenter(
            navigator = navigator,
            aiChatRepository = aiRepository,
        )
    }

    @Test
    fun `present - navigates to ModelProviderSelector when no models have non-null scope`() = runTest {
        val testPresenter = setupPresenter( listOf(AIModelProviderStatus.Gemini(null)))

        testPresenter.test {
            awaitItem()
            assertEquals(ModelProviderSelectorScreen, navigator.awaitResetRoot().newRoot)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `present - navigates to ConversationList when model is configured`() = runTest {
        val presenter = setupPresenter(
            availableModels = listOf(
                AIModelProviderStatus.Gemini(AIModelScope.GeminiModelScope("test-api-key")),
            )
        )

        presenter.test {
            awaitItem()
            assertEquals(ConversationListScreen, navigator.awaitResetRoot().newRoot)
            cancelAndConsumeRemainingEvents()
        }
    }
}