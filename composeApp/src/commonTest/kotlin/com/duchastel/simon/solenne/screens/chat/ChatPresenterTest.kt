package com.duchastel.simon.solenne.screens.chat

import com.duchastel.simon.solenne.data.chat.models.ChatMessage
import com.duchastel.simon.solenne.fakes.ChatMessagesFake
import com.duchastel.simon.solenne.fakes.FakeAiChatRepository
import com.duchastel.simon.solenne.fakes.FakeChatMessageRepository
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
    private lateinit var presenter: ChatPresenter

    @BeforeTest
    fun setup() {
        val screen = ChatScreen(CONVERSATION_ID)
        aiRepository = FakeAiChatRepository(
            initialMessages = mapOf(CONVERSATION_ID to ChatMessagesFake.chatMessages),
        )
        chatRepository = FakeChatMessageRepository()
        navigator = FakeNavigator(screen)
        presenter = ChatPresenter(
            navigator = navigator,
            screen = screen,
            chatRepository = chatRepository,
            aiChatRepository = aiRepository,
        )
    }

    @Test
    fun `present - emits initial empty state then list of messages`() = runTest {
        presenter.test {
            val initial = awaitItem()
            assertEquals(
                expected = 0,
                actual = initial.messages.size,
            )

            val withMessages = expectMostRecentItem()
            assertEquals(
                expected = ChatMessagesFake.chatMessages.size,
                actual = withMessages.messages.size,
            )
        }
    }

    @Test
    fun `present - send button enabled when text input is not blank`() =
        runTest {
            presenter.test {
                val initial = expectMostRecentItem()
                val eventSink = initial.eventSink

                eventSink(ChatScreen.Event.TextInputChanged("hello"))
                val afterTextInputChanged = expectMostRecentItem()

                assertTrue(afterTextInputChanged.sendButtonEnabled)
                assertEquals("hello", afterTextInputChanged.textInput)
            }
        }

    @Test
    fun `present - send message clears text input`() = runTest {
        presenter.test {
            val initial = awaitItem()
            val eventSink = initial.eventSink

            eventSink(ChatScreen.Event.TextInputChanged("to send"))
            eventSink(ChatScreen.Event.SendMessage("to send"))

            val state = expectMostRecentItem()
            assertTrue(state.textInput.isEmpty())
            assertFalse(state.sendButtonEnabled)
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

            val state = expectMostRecentItem()
            state.eventSink(ChatScreen.Event.TextInputChanged(messageToSend))
            state.eventSink(ChatScreen.Event.SendMessage(messageToSend))

            expectMostRecentItem()
            val sentMessages = aiRepository.getMessagesSent(CONVERSATION_ID)
            assertNotNull(sentMessages)
            assertTrue(sentMessages.any { it is ChatMessage.Text && it.text == messageToSend })
        }
    }
}