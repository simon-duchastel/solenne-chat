package com.duchastel.simon.solenne.data.ai

import com.duchastel.simon.solenne.data.chat.ChatMessage
import com.duchastel.simon.solenne.data.chat.ChatMessageRepositoryImpl
import com.duchastel.simon.solenne.data.chat.MessageAuthor
import com.duchastel.simon.solenne.dispatchers.IODispatcher
import com.duchastel.simon.solenne.network.ai.AiChatApi
import com.duchastel.simon.solenne.network.ai.Content
import com.duchastel.simon.solenne.network.ai.GenerateContentRequest
import com.duchastel.simon.solenne.network.ai.Part
import com.duchastel.simon.solenne.network.ai.gemini.GEMINI
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.Named
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
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
        withContext(IODispatcher) {
            chatMessageRepositoryImpl.addMessageToConversation(
                conversationId = conversationId,
                author = MessageAuthor.User,
                text = text,
            )

            val conversationContents = chatMessageRepositoryImpl.getMessageFlowForConversation(conversationId)
                .first()
                .map { message ->
                    Content(
                        parts = listOf(Part(message.text)),
                        role = when (message.author) {
                            is MessageAuthor.User -> "user"
                            is MessageAuthor.AI -> "model"
                        },
                    )
                }

            var messageId: String? = null
            var responseSoFar = ""
            aiChatApi.generateStreamingResponseForConversation(
                GenerateContentRequest(contents = conversationContents)
            ).collect {
                responseSoFar += it.candidates
                    .firstOrNull()?.content?.parts?.firstOrNull()?.text
                    ?: "Error: No response from AI"

                val currentMessageId = messageId
                if (currentMessageId == null) {
                    messageId = chatMessageRepositoryImpl.addMessageToConversation(
                        conversationId = conversationId,
                        author = MessageAuthor.AI,
                        text = responseSoFar,
                    )
                } else {
                    chatMessageRepositoryImpl.modifyMessageFromConversation(
                        messageId = currentMessageId,
                        conversationId = conversationId,
                        newText = responseSoFar,
                    )
                }
            }
        }
    }
}