package com.duchastel.simon.solenne.screens.modelproviderselector

import com.duchastel.simon.solenne.data.ai.AIModelProvider
import com.duchastel.simon.solenne.data.ai.AIModelProviderStatus
import com.duchastel.simon.solenne.fakes.FakeAiChatRepository
import com.duchastel.simon.solenne.screens.conversationlist.ConversationListScreen
import com.duchastel.simon.solenne.screens.modelproviderconfig.ModelProviderConfigPresenter
import com.duchastel.simon.solenne.screens.modelproviderconfig.ModelProviderConfigScreen
import com.slack.circuit.test.FakeNavigator
import com.slack.circuit.test.test
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ModelProviderSelectorPresenterTest {

    private lateinit var navigator: FakeNavigator
    private lateinit var aiRepository: FakeAiChatRepository
    private lateinit var presenter: ModelProviderSelectorPresenter

    @BeforeTest
    fun setup() {
        val screen = ModelProviderSelectorScreen
        val availableModels = listOf(
            AIModelProviderStatus.OpenAI(scope = null),
            AIModelProviderStatus.Anthropic(scope = null),
            AIModelProviderStatus.Gemini(scope = null)
        )

        aiRepository = FakeAiChatRepository(availableModels = availableModels)
        navigator = FakeNavigator(screen)
        presenter = ModelProviderSelectorPresenter(
            navigator = navigator,
            aiChatRepository = aiRepository
        )
    }

    @Test
    fun `present - emits models from repository with Other option`() = runTest {
        presenter.test {
            val state = expectMostRecentItem()

            // Verify we have the expected number of models
            // 3 from repository
            assertEquals(3, state.models.size)

            // Verify the models are correctly mapped
            assertTrue(state.models.contains(UiModelProvider.OpenAI))
            assertTrue(state.models.contains(UiModelProvider.Anthropic))
            assertTrue(state.models.contains(UiModelProvider.Gemini))

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `present - back pressed event pops navigator`() = runTest {
        presenter.test {
            val state = expectMostRecentItem()
            val eventSink = state.eventSink

            eventSink(ModelProviderSelectorScreen.Event.BackPressed)

            // Verify navigator was popped
            navigator.awaitPop()

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `present - model selected event navigates to config screen`() = runTest {
        presenter.test {
            val state = expectMostRecentItem()
            val eventSink = state.eventSink

            eventSink(ModelProviderSelectorScreen.Event.ModelSelected(UiModelProvider.Gemini))

            assertEquals(ModelProviderConfigScreen(AIModelProvider.Gemini), navigator.awaitNextScreen())
            cancelAndConsumeRemainingEvents()
        }
    }
}