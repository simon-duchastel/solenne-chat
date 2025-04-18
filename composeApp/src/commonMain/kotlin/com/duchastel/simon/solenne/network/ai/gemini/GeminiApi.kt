package com.duchastel.simon.solenne.network.ai.gemini

import com.duchastel.simon.solenne.network.ai.AiChatApi
import com.duchastel.simon.solenne.network.ai.GenerateContentRequest
import com.duchastel.simon.solenne.network.ai.GenerateContentResponse
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.Named
import io.ktor.client.HttpClient
import io.ktor.client.plugins.sse.sse
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json

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
    private val apiKey: String = "AIzaSyAKysnnaT2DgSAetvTYfkPUnWmysaN0lVo",
) : AiChatApi {

    private val geminiJson = Json {
        isLenient = true
        ignoreUnknownKeys = true
    }

    override fun generateResponseForConversation(
        request: GenerateContentRequest,
    ): Flow<GenerateContentResponse> = channelFlow {
        val url = "$BASE_URL$MODEL_NAME:streamGenerateContent?alt=sse&key=$apiKey"
        httpClient.sse(
            urlString = url,
            request = {
                method = HttpMethod.Post
                contentType(ContentType.Application.Json)
                setBody(request)
            }
        ) {
            incoming.collect { event ->
                val data = event.data
                if (data != null) {
                    val result = geminiJson.decodeFromString<GenerateContentResponse>(data)
                    send(result)
                }
            }
            println("TODO - all done")
        }
    }
}
