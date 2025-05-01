package com.duchastel.simon.solenne.screens.chat

import com.duchastel.simon.solenne.data.chat.models.ChatMessage
import com.duchastel.simon.solenne.data.tools.McpRepository
import com.duchastel.simon.solenne.fakes.ChatMessagesFake
import com.duchastel.simon.solenne.fakes.FakeAiChatRepository
import com.duchastel.simon.solenne.fakes.FakeChatMessageRepository
import com.duchastel.simon.solenne.fakes.FakeMcpRepository
import com.slack.circuit.test.FakeNavigator
import com.slack.circuit.test.test
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ChatPresenterTest {
    companion object {
        private const val CONVERSATION_ID = "presenter-test-convo"
    }

    private lateinit var navigator: FakeNavigator
    private lateinit var aiRepository: FakeAiChatRepository
    private lateinit var chatRepository: FakeChatMessageRepository
    private lateinit var mcpRepository: McpRepository
    private lateinit var presenter: ChatPresenter

    @BeforeTest
    fun setup() {
        val screen = ChatScreen(CONVERSATION_ID)
        aiRepository = FakeAiChatRepository(
            initialMessages = mapOf(CONVERSATION_ID to ChatMessagesFake.chatMessages),
        )
        chatRepository = FakeChatMessageRepository()
        mcpRepository = FakeMcpRepository()
        navigator = FakeNavigator(screen)
        presenter = ChatPresenter(
            navigator = navigator,
            screen = screen,
            chatRepository = chatRepository,
            aiChatRepository = aiRepository,
            mcpRepository = mcpRepository,
        )
    }

    @Test
    fun `present - emits initial empty state then list of messages`() = runTest {
        presenter.test {
            val initial = awaitItem()
            assertEquals(
                expected = 1, // add 1 for mcp server test message
                actual = initial.messages.size,
            )

            val withMessages = expectMostRecentItem()
            assertEquals(
                expected = ChatMessagesFake.chatMessages.size + 1, // add 1 for mcp server test message
                actual = withMessages.messages.size,
            )
        }
    }

    @Test
    fun `present - send button enabled after api key populated and text input is not blank`() =
        runTest {
            presenter.test {
                val initial = expectMostRecentItem()
                val eventSink = initial.eventSink

            eventSink(ChatScreen.Event.TextInputChanged("hello"))
            val afterTextInputChanged = awaitItem()
            assertFalse(afterTextInputChanged.sendButtonEnabled)

            eventSink(ChatScreen.Event.ApiKeySubmitted("key"))
            val state = expectMostRecentItem()
            assertTrue(state.sendButtonEnabled)
            assertEquals("hello", state.textInput)
        }
    }

    @Test
    fun `present - send message clears text input`() = runTest {
        presenter.test {
            val initial = awaitItem()
            val eventSink = initial.eventSink

            eventSink(ChatScreen.Event.ApiKeySubmitted("key"))
            eventSink(ChatScreen.Event.TextInputChanged("to send"))
            eventSink(ChatScreen.Event.SendMessage("to send"))

            val state = expectMostRecentItem()

            assertTrue(state.textInput.isEmpty())
            assertFalse(state.sendButtonEnabled)
        }
    }


    @Test
    fun `present - api key submission enables send button with text`() = runTest {
        presenter.test {
            val initial = awaitItem()
            val eventSink = initial.eventSink

            // Set some text input first
            eventSink(ChatScreen.Event.TextInputChanged("hello"))
            val withTextOnly = awaitItem()
            assertFalse(withTextOnly.sendButtonEnabled)

            // Submit API key
            eventSink(ChatScreen.Event.ApiKeySubmitted("test-api-key"))

            // Verify send button is enabled with text
            val withApiKeyAndText = expectMostRecentItem()
            assertTrue(withApiKeyAndText.sendButtonEnabled)
        }
    }

    @Test
    fun `present - API key change updates the key in state`() = runTest {
        presenter.test {
            val initial = awaitItem()
            val eventSink = initial.eventSink
            val apiKey = "test-api-key"

            // Change API key
            eventSink(ChatScreen.Event.ApiKeyChanged(apiKey))

            val state = expectMostRecentItem()
            assertEquals(apiKey, state.apiKey)
        }
    }

    @Test
    fun `present - back press triggers navigation pop`() = runTest {
        presenter.test {
            val initial = awaitItem()
            val eventSink = initial.eventSink

            eventSink(ChatScreen.Event.BackPressed)

            navigator.awaitPop()

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `present - send message adds message to repository`() = runTest {
        presenter.test {
            val messageToSend = "new test message"

            expectMostRecentItem().eventSink(ChatScreen.Event.ApiKeySubmitted("key"))
            expectMostRecentItem().eventSink(ChatScreen.Event.TextInputChanged(messageToSend))
            expectMostRecentItem().eventSink(ChatScreen.Event.SendMessage(messageToSend))

            expectMostRecentItem()

            val sentMessages = aiRepository.getMessagesSent(CONVERSATION_ID)
            assertNotNull(sentMessages)
            assertTrue(sentMessages.any { it is ChatMessage.Text && it.text == messageToSend })
        }
    }
}