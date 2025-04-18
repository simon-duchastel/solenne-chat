package com.duchastel.simon.solenne.data

import app.cash.turbine.test
import com.duchastel.simon.solenne.data.chat.ChatMessageRepositoryImpl
import com.duchastel.simon.solenne.data.chat.MessageAuthor
import com.duchastel.simon.solenne.db.chat.DbMessage
import com.duchastel.simon.solenne.fakes.FakeAiChatApi
import com.duchastel.simon.solenne.fakes.FakeChatMessageDb
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import com.duchastel.simon.solenne.data.chat.toChatMessage

internal class ChatMessageRepositoryImplTest {

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
                        content = "user-text",
                        timestamp = 123L
                    ),
                    DbMessage(
                        id = "2",
                        conversationId = conversationId,
                        author = 1L,
                        content = "ai-text",
                        timestamp = 456L,
                    )
                )
            )
        )

        val chats = repo.getMessageFlowForConversation(conversationId).first()
        assertEquals(2, chats.size)

        val first = chats[0]
        assertEquals("1", first.id)
        assertEquals("user-text", first.text)
        assertEquals(MessageAuthor.User, first.author)

        val second = chats[1]
        assertEquals("2", second.id)
        assertEquals("ai-text", second.text)
        assertEquals(MessageAuthor.AI, second.author)
    }

    @Test
    fun `addMessageToConversation - success`() = runTest {
        val conversationId = "conv-id"
        repo.addMessageToConversation(
            conversationId = conversationId,
            author = MessageAuthor.User,
            text = "hello there",
        )

        fakeDb.getMessagesForConversation(conversationId).test {
            val dbMessages = awaitItem()
            assertEquals(1, dbMessages.size)

            val userMsg = dbMessages[0]
            assertEquals(conversationId, userMsg.conversationId)
            assertEquals(0L, userMsg.author)
            assertEquals("hello there", userMsg.content)
        }
    }

    @Test
    fun `DbMessage toChatMessage - User maps correctly`() {
        val id = "1"
        val content = "hello"
        val dbMessage = DbMessage(
            id = id,
            conversationId = "conv",
            author = 0L,
            content = content,
            timestamp = 0L
        )
        val chatMessage = dbMessage.toChatMessage()
        assertEquals(id, chatMessage.id)
        assertEquals(content, chatMessage.text)
        assertEquals(MessageAuthor.User, chatMessage.author)
    }

    @Test
    fun `DbMessage toChatMessage - AI maps correctly`() {
        val id = "2"
        val content = "hi from AI"
        val dbMessage = DbMessage(
            id = id,
            conversationId = "conv",
            author = 1L,
            content = content,
            timestamp = 0L
        )
        val chatMessage = dbMessage.toChatMessage()
        assertEquals(id, chatMessage.id)
        assertEquals(content, chatMessage.text)
        assertEquals(MessageAuthor.AI, chatMessage.author)
    }

    @Test
    fun `DbMessage toChatMessage - unknown author throws`() {
        val dbMessage = DbMessage(
            id = "3",
            conversationId = "conv",
            author = 2L,
            content = "unknown",
            timestamp = 0L
        )
        assertFailsWith<IllegalStateException> {
            dbMessage.toChatMessage()
        }
    }
}