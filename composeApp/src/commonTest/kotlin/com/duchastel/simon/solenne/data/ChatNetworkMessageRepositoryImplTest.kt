package com.duchastel.simon.solenne.data

import app.cash.turbine.test
import com.duchastel.simon.solenne.data.chat.ChatMessage
import com.duchastel.simon.solenne.data.chat.ChatMessageRepositoryImpl
import com.duchastel.simon.solenne.data.chat.MessageAuthor
import com.duchastel.simon.solenne.db.chat.DbMessage
import com.duchastel.simon.solenne.db.chat.DbMessageContent
import com.duchastel.simon.solenne.fakes.FakeAiChatApi
import com.duchastel.simon.solenne.fakes.FakeChatMessageDb
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

internal class ChatNetworkMessageRepositoryImplTest {

    private lateinit var fakeDb: FakeChatMessageDb
    private lateinit var fakeAi: FakeAiChatApi

    private lateinit var repo: ChatMessageRepositoryImpl

    @BeforeTest
    fun setup() {
        initRepo()
    }

    private fun initRepo(
        initialDbMessages: Map<String, List<DbMessage>> = emptyMap(),
        geminiResponse: String = "Gemini response",
    ) {
        fakeDb = FakeChatMessageDb(initialDbMessages)
        fakeAi = FakeAiChatApi(fakeResponse = geminiResponse)
        repo = ChatMessageRepositoryImpl(
            chatMessageDb = fakeDb,
        )
    }

    @Test
    fun `getMessagesForConversation - success`() = runTest {
        val conversationId = "conv-id"
        initRepo(
            initialDbMessages = mapOf(
                conversationId to listOf(
                    DbMessage(
                        id = "1",
                        conversationId = conversationId,
                        author = 0L,
                        content = DbMessageContent.Text("user-text"),
                        timestamp = 123L
                    ),
                    DbMessage(
                        id = "2",
                        conversationId = conversationId,
                        author = 1L,
                        content = DbMessageContent.Text("ai-text"),
                        timestamp = 456L,
                    )
                )
            )
        )

        val chats = repo.getMessageFlowForConversation(conversationId).first()
        assertEquals(2, chats.size)

        val first = chats[0]
        assertEquals("1", first.id)
        assertIs<ChatMessage.Text>(first)
        assertEquals("user-text", (first as ChatMessage.Text).text)
        assertEquals(MessageAuthor.User, first.author)

        val second = chats[1]
        assertEquals("2", second.id)
        assertIs<ChatMessage.Text>(second)
        assertEquals("ai-text", (second as ChatMessage.Text).text)
        assertEquals(MessageAuthor.AI, second.author)
    }

    @Test
    fun `addMessageToConversation - success`() = runTest {
        val conversationId = "conv-id"
        val sentMessage = repo.addTextMessageToConversation(
            conversationId = conversationId,
            author = MessageAuthor.User,
            text = "hello there",
        )

        // Verify the message was stored in the database correctly
        fakeDb.getMessagesForConversation(conversationId).test {
            val dbMessages = awaitItem()
            assertEquals(1, dbMessages.size)

            val userMsg = dbMessages[0]
            assertEquals(conversationId, userMsg.conversationId)
            assertEquals(0L, userMsg.author)
            assertIs<DbMessageContent.Text>(userMsg.content)
            assertEquals("hello there", (userMsg.content as DbMessageContent.Text).text)
        }

        // Verify the returned message is correctly formatted
        assertIs<ChatMessage.Text>(sentMessage)
        assertEquals("hello there", (sentMessage as ChatMessage.Text).text)
        assertEquals(MessageAuthor.User, sentMessage?.author)
    }

    @Test
    fun `message author mapping - User works correctly`() = runTest {
        val conversationId = "conv-id"
        val message = repo.addTextMessageToConversation(
            conversationId = conversationId,
            author = MessageAuthor.User,
            text = "hello from user",
        )

        assertIs<ChatMessage.Text>(message)
        assertEquals("hello from user", (message as ChatMessage.Text).text)
        assertEquals(MessageAuthor.User, message.author)
    }

    @Test
    fun `message author mapping - AI works correctly`() = runTest {
        val conversationId = "conv-id"
        val message = repo.addTextMessageToConversation(
            conversationId = conversationId,
            author = MessageAuthor.AI,
            text = "hello from AI",
        )

        assertIs<ChatMessage.Text>(message)
        assertEquals("hello from AI", (message as ChatMessage.Text).text)
        assertEquals(MessageAuthor.AI, message.author)
    }
}