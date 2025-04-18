package com.duchastel.simon.solenne.network.ai.gemini

import com.duchastel.simon.solenne.network.JsonParser
import com.duchastel.simon.solenne.network.ai.AiChatApi
import com.duchastel.simon.solenne.network.ai.GenerateContentRequest
import com.duchastel.simon.solenne.network.ai.GenerateContentResponse
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.Named
import io.ktor.client.HttpClient
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
 * Currently this only supports the `generateContent` endpoint for the `gemini-2.0-flash` model.
 *
 * This target lives in the `networks` layer to keep network‑related abstractions
 * separated from repositories and database code.
 */
@Named(GEMINI)
class GeminiApi @Inject constructor(
    private val httpClient: HttpClient,
    private val apiKey: String = "<<YOUR_API_KEY>>",
) : AiChatApi {

    override fun generateResponseForConversation(
        request: GenerateContentRequest,
    ): Flow<GenerateContentResponse> = channelFlow {
        val url = "$BASE_URL$MODEL_NAME:streamGenerateContent?alt=sse&key=$apiKey"

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
