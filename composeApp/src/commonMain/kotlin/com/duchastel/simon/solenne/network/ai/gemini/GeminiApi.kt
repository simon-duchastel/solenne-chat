package com.duchastel.simon.solenne.network.ai.gemini

import com.duchastel.simon.solenne.network.ai.AiChatApi
import com.duchastel.simon.solenne.network.ai.GenerateContentRequest
import com.duchastel.simon.solenne.network.ai.GenerateContentResponse
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.Named
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/"
private const val MODEL_NAME = "gemini-2.0-flash"

/**
 * Simple wrapper around the Google Gemini generative language REST API.
 * Currently this only supports the `generateContent` endpoint for the `gemini-2.0-flash` model.
 *
 * This target lives in the `networks` layer to keep network‑related abstractions
 * separated from repositories and database code.
 */
@Named(GEMINI)
class GeminiApi @Inject constructor(
    private val httpClient: HttpClient,
    private val apiKey: String = "<<INSERT API KEY HERE>>",
) : AiChatApi {

    override suspend fun generateResponseForConversation(
        request: GenerateContentRequest,
    ): GenerateContentResponse {
        val url = "$BASE_URL$MODEL_NAME:generateContent?key=$apiKey"
        val response: GenerateContentResponse = httpClient.post(url) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

        return response
    }
}