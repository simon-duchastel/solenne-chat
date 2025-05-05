package com.duchastel.simon.solenne.screens.conversationlist

import com.duchastel.simon.solenne.data.chat.models.ChatConversation
import com.duchastel.simon.solenne.util.fakes.FakeChatMessageRepository
import com.duchastel.simon.solenne.screens.chat.ChatScreen
import com.duchastel.simon.solenne.screens.conversationlist.ConversationListScreen.Event
import com.slack.circuit.test.FakeNavigator
import com.slack.circuit.test.test
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class ConversationListPresenterTest {

    private lateinit var navigator: FakeNavigator
    private lateinit var chatRepository: FakeChatMessageRepository
    private lateinit var presenter: ConversationListPresenter

    @BeforeTest
    fun setup() {
        val screen = ConversationListScreen
        val initialConversations = listOf(
            ChatConversation(id = "conversation-1"),
            ChatConversation(id = "conversation-2"),
        )
        chatRepository = FakeChatMessageRepository(initialConversations = initialConversations)
        navigator = FakeNavigator(screen)
        presenter = ConversationListPresenter(
            navigator = navigator,
            chatRepository = chatRepository,
        )
    }

    @Test
    fun `present - emits initial state`() = runTest {
        presenter.test {
            val state = awaitItem()
            assertEquals(
                expected = persistentListOf(),
                actual = state.conversations,
            )

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `present - emits conversation list`() = runTest {
        presenter.test {
            val state = expectMostRecentItem()

            assertEquals(
                expected = persistentListOf("conversation-1", "conversation-2"),
                actual = state.conversations,
            )
        }
    }

    @Test
    fun `present - conversation clicked navigates to chat screen`() = runTest {
        presenter.test {
            val state = awaitItem()
            val eventSink = state.eventSink

            eventSink(Event.ConversationClicked("conversation-1"))

            val fakeNavigator = navigator
            val destination = fakeNavigator.awaitNextScreen()
            assertIs<ChatScreen>(destination)
            assertEquals("conversation-1", destination.conversationId)

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `present - new conversation clicked creates new conversation`() = runTest {
        presenter.test {
            val state = awaitItem()
            val eventSink = state.eventSink

            assertEquals(2, chatRepository.getAvailableConversationsFlow().first().size)

            eventSink(Event.NewConversationClicked)
            assertEquals(3, chatRepository.getAvailableConversationsFlow().first().size)
            cancelAndConsumeRemainingEvents()
        }
    }
}