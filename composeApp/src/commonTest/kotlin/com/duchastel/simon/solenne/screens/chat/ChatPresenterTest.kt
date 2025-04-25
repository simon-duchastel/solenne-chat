package com.duchastel.simon.solenne.screens.chat

import com.duchastel.simon.solenne.fakes.ChatMessagesFake
import com.duchastel.simon.solenne.fakes.FakeAiChatRepository
import com.duchastel.simon.solenne.fakes.FakeMcpRepository
import com.slack.circuit.test.test
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ChatPresenterTest {

    @Test
    fun `present - emits initial empty state then list of messages`() = runTest {
        val conversationId = "presenter-test-convo"
        val repository = FakeAiChatRepository(
            initialMessages = mapOf(conversationId to ChatMessagesFake.chatMessages),
        )
        val presenter = ChatPresenter(
            aiChatRepository = repository,
            mcpRepository = FakeMcpRepository(),
            screen = ChatScreen(conversationId),
        )

        presenter.test {
            val first = expectMostRecentItem()
            assertEquals(
                expected = 1, // add 1 for mcp server test message
                actual = first.messages.size,
            )

            val second = expectMostRecentItem()
            assertEquals(
                expected = ChatMessagesFake.chatMessages.size + 1, // add 1 for mcp server test message
                actual = second.messages.size,
            )
        }
    }

    @Test
    fun `present - send button enabled after api key populated and text input is not blank`() = runTest {
        val conversationId = "convo-send-enabled"
        val repository = FakeAiChatRepository(
            initialMessages = mapOf(conversationId to ChatMessagesFake.chatMessages)
        )
        val presenter = ChatPresenter(
            aiChatRepository = repository,
            mcpRepository = FakeMcpRepository(),
            screen = ChatScreen(conversationId)
        )

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
        val conversationId = "convo-send-clear"
        val repository = FakeAiChatRepository(
            initialMessages = mapOf(conversationId to ChatMessagesFake.chatMessages)
        )
        val presenter = ChatPresenter(
            aiChatRepository = repository,
            mcpRepository = FakeMcpRepository(),
            screen = ChatScreen(conversationId)
        )

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
