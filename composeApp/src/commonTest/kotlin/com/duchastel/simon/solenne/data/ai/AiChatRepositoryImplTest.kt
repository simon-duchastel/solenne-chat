package com.duchastel.simon.solenne.data.ai

import app.cash.turbine.test
import com.duchastel.simon.solenne.data.chat.ChatMessageRepositoryImpl
import com.duchastel.simon.solenne.data.chat.models.ChatMessage
import com.duchastel.simon.solenne.data.chat.models.MessageAuthor
import com.duchastel.simon.solenne.data.tools.McpRepository
import com.duchastel.simon.solenne.db.chat.DbMessage
import com.duchastel.simon.solenne.db.chat.DbMessageContent
import com.duchastel.simon.solenne.util.fakes.FAKE_AI_MODEL_SCOPE
import com.duchastel.simon.solenne.util.fakes.FakeAiApiKeyDb
import com.duchastel.simon.solenne.util.fakes.FakeAiChatApi
import com.duchastel.simon.solenne.util.fakes.FakeChatMessageDb
import com.duchastel.simon.solenne.util.fakes.FakeMcpRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class AiChatRepositoryImplTest {

    private lateinit var fakeChatRepo: ChatMessageRepositoryImpl
    private lateinit var fakeDb: FakeChatMessageDb
    private lateinit var fakeAiApi: FakeAiChatApi
    private lateinit var fakeMcpRepo: McpRepository
    private lateinit var fakeAiApiKeyDb: FakeAiApiKeyDb
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
        fakeMcpRepo = FakeMcpRepository()
        fakeAiApiKeyDb = FakeAiApiKeyDb()

        aiChatRepo = AiChatRepositoryImpl(
            aiApiKeyDb = fakeAiApiKeyDb,
            chatMessageRepository = fakeChatRepo,
            mcpRepository = fakeMcpRepo,
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
                        content = DbMessageContent.Text("Hello AI"),
                        timestamp = 100L
                    ),
                    DbMessage(
                        id = "2",
                        conversationId = conversationId,
                        author = 1L, // AI
                        content = DbMessageContent.Text("Hello human"),
                        timestamp = 200L
                    )
                )
            )
        )

        fakeChatRepo.getMessageFlowForConversation(conversationId).test {
            val messages = awaitItem()
            assertEquals(2, messages.size)
            assertEquals("Hello AI", (messages[0] as ChatMessage.Text).text)
            assertEquals(MessageAuthor.User, messages[0].author)
            assertEquals("Hello human", (messages[1] as ChatMessage.Text).text)
            assertEquals(MessageAuthor.AI, messages[1].author)
            cancelAndConsumeRemainingEvents()
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
            assertEquals(userMessage, (dbMessages[0].content as DbMessageContent.Text).text)
            assertEquals(0L, dbMessages[0].author) // User
            assertEquals(aiResponseText, (dbMessages[1].content as DbMessageContent.Text).text)
            assertEquals(1L, dbMessages[1].author) // AI
            cancelAndConsumeRemainingEvents()
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
                        content = DbMessageContent.Text("First question"),
                        timestamp = 100L
                    ),
                    DbMessage(
                        id = "2",
                        conversationId = conversationId,
                        author = 1L, // AI
                        content = DbMessageContent.Text("First answer"),
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
            assertEquals("First question", (messages[0].content as DbMessageContent.Text).text)
            assertEquals("First answer", (messages[1].content as DbMessageContent.Text).text)

            // new messages are added
            assertEquals("Second question", (messages[2].content as DbMessageContent.Text).text)
            assertEquals("AI response", (messages[3].content as DbMessageContent.Text).text)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `getAvailableModelsFlow - returns available AI model providers`() = runTest {
        aiChatRepo.getAvailableModelsFlow().test {
            val modelProviders = awaitItem()
            assertEquals(1, modelProviders.size)

            // Verify all expected providers are present
            assertTrue(modelProviders.any { it is AIModelProviderStatus.Gemini })

            cancelAndConsumeRemainingEvents()
        }
    }
}