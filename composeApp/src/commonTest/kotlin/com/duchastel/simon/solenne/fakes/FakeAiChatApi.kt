package com.duchastel.simon.solenne.fakes

import com.duchastel.simon.solenne.network.ai.AiChatApi

/**
 * A fake GeminiApi that always returns [fakeResponse], ignoring the prompt.
 */
internal class FakeAiChatApi(
    private val fakeResponse: String = "fake-ai-response"
) : AiChatApi {
    override suspend fun generateContent(prompt: String): String {
        return fakeResponse
    }
}
