package com.duchastel.simon.solenne.fakes

import com.duchastel.simon.solenne.data.chat.ChatMessage
import com.duchastel.simon.solenne.data.chat.ChatMessageRepository
import com.duchastel.simon.solenne.data.chat.MessageAuthor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

internal class FakeChatMessageRepository(
    private val initialMessages: List<ChatMessage> = ChatMessagesFake.chatMessages,
): ChatMessageRepository {

    override fun getMessageFlowForConversation(
        conversationId: String,
    ): Flow<List<ChatMessage>> = flowOf(initialMessages)

    override suspend fun addMessageToConversation(
        conversationId: String,
        author: MessageAuthor,
        text: String,
    ): String {
        return "do-not-rely-on-this-id"
    }

    override suspend fun modifyMessageFromConversation(
        conversationId: String,
        messageId: String,
        newText: String
    ): String {
        return messageId
    }
}