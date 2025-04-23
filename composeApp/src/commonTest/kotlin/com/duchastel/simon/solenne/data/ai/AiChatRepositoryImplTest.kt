package com.duchastel.simon.solenne.data.ai

import app.cash.turbine.test
import com.duchastel.simon.solenne.data.chat.ChatMessageRepositoryImpl
import com.duchastel.simon.solenne.data.chat.MessageAuthor
import com.duchastel.simon.solenne.db.chat.DbMessage
import com.duchastel.simon.solenne.fakes.FakeAiChatApi
import com.duchastel.simon.solenne.fakes.FakeChatMessageDb
import com.duchastel.simon.solenne.fakes.FAKE_AI_MODEL_SCOPE
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

internal class AiChatRepositoryImplTest {

    private lateinit var fakeChatRepo: ChatMessageRepositoryImpl
    private lateinit var fakeDb: FakeChatMessageDb
    private lateinit var fakeAiApi: FakeAiChatApi
    private lateinit var aiChatRepo: AiChatRepositoryImpl

    @BeforeTest
    fun setup() {
        initRepo()
    }

    private fun initRepo(
        initialDbMessages: Map<String, List<DbMessage>> = emptyMap(),
        aiResponse: String = "AI response"
    ) {
        fakeDb = FakeChatMessageDb(initialDbMessages)
        fakeChatRepo = ChatMessageRepositoryImpl(fakeDb)
        fakeAiApi = FakeAiChatApi(fakeResponse = aiResponse)

        aiChatRepo = AiChatRepositoryImpl(
            chatMessageRepository = fakeChatRepo,
            geminiApi = fakeAiApi
        )
    }

    @Test
    fun `getMessageFlowForConversation - delegates to ChatMessageRepository`() = runTest {
        val conversationId = "test-conv-id"
        initRepo(
            initialDbMessages = mapOf(
                conversationId to listOf(
                    DbMessage(
                        id = "1",
                        conversationId = conversationId,
                        author = 0L, // User
                        content = "Hello AI",
                        timestamp = 100L
                    ),
                    DbMessage(
                        id = "2",
                        conversationId = conversationId,
                        author = 1L, // AI
                        content = "Hello human",
                        timestamp = 200L
                    )
                )
            )
        )

        aiChatRepo.getMessageFlowForConversation(conversationId).test {
            val messages = awaitItem()
            assertEquals(2, messages.size)
            assertEquals("Hello AI", messages[0].text)
            assertEquals(MessageAuthor.User, messages[0].author)
            assertEquals("Hello human", messages[1].text)
            assertEquals(MessageAuthor.AI, messages[1].author)
        }
    }

    @Test
    fun `sendTextMessageFromUserToConversation - adds user message and AI response`() = runTest {
        val conversationId = "test-conv-id"
        val userMessage = "How does AI work?"
        val aiResponseText = "AI works through complex algorithms"

        initRepo(aiResponse = aiResponseText)

        aiChatRepo.sendTextMessageFromUserToConversation(
            aiModelScope = FAKE_AI_MODEL_SCOPE,
            conversationId = conversationId,
            text = userMessage,
        )

        fakeDb.getMessagesForConversation(conversationId).test {
            val dbMessages = awaitItem()
            assertEquals(2, dbMessages.size)
            assertEquals(userMessage, dbMessages[0].content)
            assertEquals(0L, dbMessages[0].author) // User
            assertEquals(aiResponseText, dbMessages[1].content)
            assertEquals(1L, dbMessages[1].author) // AI
        }
    }

    @Test
    fun `sendTextMessageFromUserToConversation - sends conversation history to AI`() = runTest {
        val conversationId = "test-conv-id"
        initRepo(
            initialDbMessages = mapOf(
                conversationId to listOf(
                    DbMessage(
                        id = "1",
                        conversationId = conversationId,
                        author = 0L, // User
                        content = "First question",
                        timestamp = 100L
                    ),
                    DbMessage(
                        id = "2",
                        conversationId = conversationId,
                        author = 1L, // AI
                        content = "First answer",
                        timestamp = 200L
                    )
                )
            )
        )

        aiChatRepo.sendTextMessageFromUserToConversation(
            FAKE_AI_MODEL_SCOPE,
            conversationId,
            "Second question",
        )

        fakeDb.getMessagesForConversation(conversationId).test {
            val messages = awaitItem()
            assertEquals(4, messages.size)

            // First two messages are unchanged
            assertEquals("First question", messages[0].content)
            assertEquals("First answer", messages[1].content)

            // new messages are added
            assertEquals("Second question", messages[2].content)
            assertEquals("AI response", messages[3].content)
        }
    }
}