package com.duchastel.simon.solenne.fakes

import com.duchastel.simon.solenne.network.ai.AiChatApi
import com.duchastel.simon.solenne.network.ai.Candidate
import com.duchastel.simon.solenne.network.ai.Content
import com.duchastel.simon.solenne.network.ai.GenerateContentRequest
import com.duchastel.simon.solenne.network.ai.GenerateContentResponse
import com.duchastel.simon.solenne.network.ai.Part
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * A fake AiChatApi that always returns [fakeResponse], ignoring the request content.
 */
internal class FakeAiChatApi(
    private val fakeResponse: String = "fake-ai-response"
) : AiChatApi {
    
    override suspend fun generateResponseForConversation(
        request: GenerateContentRequest
    ): GenerateContentResponse {
        return GenerateContentResponse(
            candidates = listOf(
                Candidate(
                    content = Content(
                        parts = listOf(Part(fakeResponse)),
                        role = "model"
                    )
                )
            )
        )
    }
    
    override fun generateStreamingResponseForConversation(
        request: GenerateContentRequest
    ): Flow<GenerateContentResponse> {
        return flowOf(
            GenerateContentResponse(
                candidates = listOf(
                    Candidate(
                        content = Content(
                            parts = listOf(Part(fakeResponse)),
                            role = "model"
                        )
                    )
                )
            )
        )
    }
}