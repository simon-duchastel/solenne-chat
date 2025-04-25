package com.duchastel.simon.solenne.network.ai.gemini

import com.duchastel.simon.solenne.data.ai.AIModelScope.GeminiModelScope
import com.duchastel.simon.solenne.network.JsonParser
import com.duchastel.simon.solenne.network.ai.AiChatApi
import com.duchastel.simon.solenne.network.ai.Conversation
import com.duchastel.simon.solenne.network.ai.ConversationResponse
import com.duchastel.simon.solenne.network.ai.GenerateContentRequest
import com.duchastel.simon.solenne.network.ai.GenerateContentResponse
import com.duchastel.simon.solenne.network.ai.Tool
import dev.zacsweers.metro.Inject
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import io.ktor.utils.io.readUTF8Line
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow

private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/"
private const val MODEL_NAME = "gemini-2.0-flash"

/**
 * Simple wrapper around the Google Gemini generative language REST API.
 * Currently this only supports the `generateContent` and `streamGenerateContent`
 * endpoints for the `gemini-2.0-flash` model.
 */
class GeminiApi @Inject constructor(
    private val httpClient: HttpClient,
) : AiChatApi<GeminiModelScope> {

    override suspend fun generateResponseForConversation(
        scope: GeminiModelScope,
        conversation: Conversation,
        systemPrompt: String?,
        tools: List<Tool>,
    ): ConversationResponse {
        val url = "$BASE_URL$MODEL_NAME:generateContent?key=${scope.apiKey}"
        val response: GenerateContentResponse = httpClient.post(url) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

        return response
    }

    override fun generateStreamingResponseForConversation(
        scope: GeminiModelScope,
        conversation: Conversation,
        systemPrompt: String?,
        tools: List<Tool>,
    ): Flow<ConversationResponse> = channelFlow {
        val url = "$BASE_URL$MODEL_NAME:streamGenerateContent?alt=sse&key=${scope.apiKey}"

        httpClient.post(url){
            method = HttpMethod.Post
            contentType(ContentType.Application.Json)
            setBody(request)
        }.bodyAsChannel()
            .let { channel ->
                val buffer = StringBuilder()

                while (!channel.isClosedForRead) {
                    val line = channel.readUTF8Line() ?: break
                    if (line.isBlank()) {
                        val sanitizedBuffer = buffer.toString().removePrefix("data:").trim()
                        val parsedData: GenerateContentResponse = JsonParser.decodeFromString(sanitizedBuffer)

                        send(parsedData)
                        buffer.clear()
                    } else {
                        buffer.appendLine(line)
                    }
                }
            }
    }
}
