package com.duchastel.simon.solenne.fakes

import com.duchastel.simon.solenne.network.ai.AiChatApi
import com.duchastel.simon.solenne.network.ai.ConversationMessage

/**
 * A fake GeminiApi that always returns [fakeResponse], ignoring the prompt or conversation history.
 */
internal class FakeAiChatApi(
    private val fakeResponse: String = "fake-ai-response"
) : AiChatApi {
    override suspend fun generateContent(prompt: String): String {
        return fakeResponse
    }

    override suspend fun generateResponseForConversation(messages: List<ConversationMessage>): String {
        return fakeResponse
    }
}