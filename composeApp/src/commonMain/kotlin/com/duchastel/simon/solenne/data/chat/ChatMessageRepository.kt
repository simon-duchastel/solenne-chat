package com.duchastel.simon.solenne.data.chat

import com.duchastel.simon.solenne.db.chat.ChatMessageDb
import com.duchastel.simon.solenne.db.chat.GetMessagesForConversation
import com.duchastel.simon.solenne.fakes.ChatMessagesFake
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

interface ChatMessageRepository {
    fun getMessagesForConversation(conversationId: String): Flow<List<ChatMessage>>
}

class ChatMessageRepositoryImpl @Inject constructor(
    private val chatMessageDb: ChatMessageDb,
): ChatMessageRepository {
    override fun getMessagesForConversation(conversationId: String): Flow<List<ChatMessage>> {
        return chatMessageDb.getMessagesForConversation(conversationId)
            .distinctUntilChanged()
            .map { query ->
                query.map(GetMessagesForConversation::toChatMessage)
            }
    }
}

fun GetMessagesForConversation.toChatMessage(): ChatMessage {
    return ChatMessage(
        id = id,
        text = content,
        author = when (author) {
            0L -> MessageAuthor.User
            1L -> MessageAuthor.AI
            else -> error("Unknown author received for GetMessagesForConversation[$this] - $author")
        }
    )
}
