package com.duchastel.simon.solenne.screens.chat

import com.duchastel.simon.solenne.fakes.ChatMessagesFake
import com.duchastel.simon.solenne.fakes.FakeAiChatRepository
import com.slack.circuit.test.test
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ChatPresenterTest {

    @Test
    fun `present - emits initial empty state then list of messages`() = runTest {
        val conversationId = "presenter-test-convo"
        val repository = FakeAiChatRepository(
            initialMessages = mapOf(conversationId to ChatMessagesFake.chatMessages),
        )
        val presenter = ChatPresenter(
            aiChatRepository = repository,
            screen = ChatScreen(conversationId),
        )

        presenter.test {
            val first = awaitItem()
            assertEquals(
                expected = 0,
                actual = first.messages.size,
            )

            val second = awaitItem()
            assertEquals(
                expected = ChatMessagesFake.chatMessages.size,
                actual = second.messages.size,
            )
        }
    }
}
