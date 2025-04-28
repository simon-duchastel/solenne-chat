package com.duchastel.simon.solenne.fakes

import com.duchastel.simon.solenne.data.chat.ChatMessage
import com.duchastel.simon.solenne.data.chat.ChatMessageRepository
import com.duchastel.simon.solenne.data.chat.MessageAuthor
import com.duchastel.simon.solenne.data.tools.McpServer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.serialization.json.JsonElement

internal class FakeChatMessageRepository(
    private val initialMessages: List<ChatMessage> = ChatMessagesFake.chatMessages,
): ChatMessageRepository {

    override fun getMessageFlowForConversation(
        conversationId: String,
    ): Flow<List<ChatMessage>> = flowOf(initialMessages)

    override suspend fun addTextMessageToConversation(
        conversationId: String,
        author: MessageAuthor,
        text: String,
    ): ChatMessage {
        return ChatMessage.Text(
            id = "do-not-rely-on-this-id",
            author = author,
            text = text
        )
    }

    override suspend fun modifyMessageFromConversation(
        conversationId: String,
        messageId: String,
        updatedText: String
    ): ChatMessage {
        return ChatMessage.Text(
            id = messageId,
            author = MessageAuthor.AI,
            text = updatedText
        )
    }

    override suspend fun addToolUseToConversation(
        conversationId: String,
        mcpServer: McpServer,
        toolName: String,
        argumentsSupplied: Map<String, JsonElement>
    ): ChatMessage {
        return ChatMessage.ToolUse(
            id = "tool-use-id",
            toolName = toolName,
            argumentsSupplied = argumentsSupplied
        )
    }

    override suspend fun addToolUseResultToConversation(
        conversationId: String,
        messageId: String,
        toolResult: ChatMessage.ToolUse.ToolResult
    ): ChatMessage {
        return ChatMessage.ToolUse(
            id = messageId,
            toolName = "fake-tool",
            argumentsSupplied = emptyMap(),
            result = toolResult
        )
    }
}