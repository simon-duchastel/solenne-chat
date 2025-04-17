package com.duchastel.simon.solenne.data

import com.duchastel.simon.solenne.data.chat.ChatMessageRepositoryImpl
import com.duchastel.simon.solenne.fakes.ChatMessagesFake
import com.duchastel.simon.solenne.fakes.FakeChatMessageDb
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

internal class ChatMessageRepositoryTest {

  private val repo = ChatMessageRepositoryImpl(
    chatMessageDb = FakeChatMessageDb(),
  )

  @Test
  fun `getMessagesForConversation returns fake messages`() = runTest {
    val list = repo.getMessagesForConversation("some-id").first()
    assertEquals(
      expected = ChatMessagesFake.chatMessages,
      actual = list
    )
  }
}
