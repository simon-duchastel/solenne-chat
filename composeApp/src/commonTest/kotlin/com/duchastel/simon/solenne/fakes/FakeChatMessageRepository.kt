package com.duchastel.simon.solenne.fakes

import com.duchastel.simon.solenne.data.chat.ChatMessage
import com.duchastel.simon.solenne.data.chat.ChatMessageRepository
import com.duchastel.simon.solenne.data.chat.MessageAuthor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

internal class FakeChatMessageRepository(
    initialMessages: List<ChatMessage> = ChatMessagesFake.chatMessages,
): ChatMessageRepository {
    private val messageFlow = MutableStateFlow(initialMessages)

    override fun getMessageFlowForConversation(
        conversationId: String,
    ): Flow<List<ChatMessage>> = messageFlow

    override suspend fun sendTextToConversation(conversationId: String, text: String) {
        messageFlow.value += ChatMessage(
            id = "do-not-rely-on-this-id",
            text = text,
            author = MessageAuthor.User,
        )
    }
}