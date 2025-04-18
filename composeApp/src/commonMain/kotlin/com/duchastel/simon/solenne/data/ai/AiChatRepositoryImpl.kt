package com.duchastel.simon.solenne.data.ai

import com.duchastel.simon.solenne.data.chat.ChatMessage
import com.duchastel.simon.solenne.data.chat.ChatMessageRepositoryImpl
import com.duchastel.simon.solenne.data.chat.MessageAuthor
import com.duchastel.simon.solenne.network.ai.AiChatApi
import com.duchastel.simon.solenne.network.ai.gemini.GEMINI
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.Named
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class AiChatRepositoryImpl @Inject constructor(
    private val chatMessageRepositoryImpl: ChatMessageRepositoryImpl,
    @Named(GEMINI) private val aiChatApi: AiChatApi,
) : AiChatRepository {
    override fun getMessageFlowForConversation(conversationId: String): Flow<List<ChatMessage>> {
        return chatMessageRepositoryImpl.getMessageFlowForConversation(conversationId)
    }

    override suspend fun sendTextMessageFromUserToConversation(
        conversationId: String,
        text: String
    ) {
        withContext(Dispatchers.Default) {
            chatMessageRepositoryImpl.addMessageToConversation(
                conversationId = conversationId,
                author = MessageAuthor.User,
                text = text,
            )
            val aiResponse = aiChatApi.generateContent(text)
            chatMessageRepositoryImpl.addMessageToConversation(
                conversationId = conversationId,
                author = MessageAuthor.AI,
                text = aiResponse,
            )
        }
    }
}