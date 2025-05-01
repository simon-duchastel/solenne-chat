package com.duchastel.simon.solenne.screens.chat

import com.duchastel.simon.solenne.fakes.ChatMessagesFake
import com.duchastel.simon.solenne.fakes.FakeAiChatRepository
import com.duchastel.simon.solenne.fakes.FakeChatMessageRepository
import com.duchastel.simon.solenne.fakes.FakeMcpRepository
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.screen.Screen
import com.slack.circuit.test.FakeNavigator
import com.slack.circuit.test.test
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ChatPresenterTest {

    private lateinit var navigator: Navigator
    private lateinit var repository: FakeAiChatRepository
    private lateinit var presenter: ChatPresenter

    @BeforeTest
    fun setup() {
        val conversationId = "presenter-test-convo"
        val screen = ChatScreen(conversationId)
        repository = FakeAiChatRepository(
            initialMessages = mapOf(conversationId to ChatMessagesFake.chatMessages),
        )
        navigator = FakeNavigator(screen)
        presenter = ChatPresenter(
            navigator = navigator,
            screen = screen,
            chatRepository = FakeChatMessageRepository(),
            aiChatRepository = repository,
            mcpRepository = FakeMcpRepository(),
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
    fun `present - send button enabled after api key populated and text input is not blank`() = runTest {
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
}
