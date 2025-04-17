package com.duchastel.simon.solenne.data

import com.duchastel.simon.solenne.fakes.ChatMessagesFake
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ChatMessageRepositoryTest {

  @Test
  fun `getMessagesForConversation returns fake messages`() = runTest {
    val repo = ChatMessageRepositoryImpl()
    val list = repo.getMessagesForConversation("some-id").first()
    assertEquals(
      expected = ChatMessagesFake.chatMessages,
      actual = list
    )
  }
}
