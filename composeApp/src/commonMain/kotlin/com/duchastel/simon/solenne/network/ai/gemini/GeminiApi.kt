package com.duchastel.simon.solenne.network.ai.gemini

import com.duchastel.simon.solenne.data.ai.AIModelScope.GeminiModelScope
import com.duchastel.simon.solenne.network.JsonParser
import com.duchastel.simon.solenne.network.ai.AiChatApi
import com.duchastel.simon.solenne.network.ai.Conversation
import com.duchastel.simon.solenne.network.ai.ConversationResponse
import com.duchastel.simon.solenne.network.ai.NetworkMessage
import com.duchastel.simon.solenne.network.ai.Tool
import com.duchastel.simon.solenne.network.wrapHttpCall
import com.duchastel.simon.solenne.util.SolenneResult
import com.duchastel.simon.solenne.util.asFailure
import com.duchastel.simon.solenne.util.asSuccess
import com.duchastel.simon.solenne.util.map
import com.duchastel.simon.solenne.util.onFailure
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
import kotlinx.serialization.json.JsonObject

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
    ): SolenneResult<ConversationResponse> {
        val url = "$BASE_URL$MODEL_NAME:generateContent?key=${scope.apiKey}"
        val request = createGenerateContentRequest(conversation, systemPrompt, tools)

         return wrapHttpCall<GenerateContentResponse> {
           httpClient.post(url) {
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body()
        }.map {
            it.toConversationResponse()
         }
    }

    override fun generateStreamingResponseForConversation(
        scope: GeminiModelScope,
        conversation: Conversation,
        systemPrompt: String?,
        tools: List<Tool>,
    ): Flow<SolenneResult<ConversationResponse>> = channelFlow {
        val url = "$BASE_URL$MODEL_NAME:streamGenerateContent?alt=sse&key=${scope.apiKey}"
        val request = createGenerateContentRequest(conversation, systemPrompt, tools)

        // wraps the call and stops emitting values in the flow if there's an error
        wrapHttpCall {
            httpClient.post(url) {
                method = HttpMethod.Post
                contentType(ContentType.Application.Json)
                setBody(request)
            }.bodyAsChannel().let { channel ->
                val buffer = StringBuilder()

                while (!channel.isClosedForRead) {
                    val line = channel.readUTF8Line() ?: break
                    if (line.isBlank()) {
                        val sanitizedBuffer = buffer.toString().removePrefix("data:").trim()
                        val parsedData: GenerateContentResponse =
                            JsonParser.decodeFromString(sanitizedBuffer)

                        send(parsedData.toConversationResponse().asSuccess())
                        buffer.clear()
                    } else {
                        buffer.appendLine(line)
                    }
                }
            }
        }.onFailure {
            send(it.asFailure())
        }
    }
}

// Extension functions to convert between AiChatModel and GeminiApiModel types

internal fun createGenerateContentRequest(
    conversation: Conversation,
    systemPrompt: String?,
    tools: List<Tool>
): GenerateContentRequest {
    val contents = conversation.networkMessages.flatMap(NetworkMessage::toContents)

    val systemInstruction = systemPrompt?.let {
        Content(
            parts = listOf(Part(text = systemPrompt)),
        )
    }

    return GenerateContentRequest(
        contents = contents,
        tools = if (tools.isEmpty()) null else listOf(tools.toGeminiTools()),
        systemInstruction = systemInstruction
    )
}

internal fun NetworkMessage.toContents(): List<Content> {
    return when (this) {
        is NetworkMessage.UserNetworkMessage -> listOf(
            Content(
                parts = listOf(Part(text = text)),
                role = "user"
            )
        )

        is NetworkMessage.AiNetworkMessage.Text -> listOf(
            Content(
                parts = listOf(Part(text = text)),
                role = "model"
            )
        )

        is NetworkMessage.AiNetworkMessage.ToolUse -> listOfNotNull(
            Content(
                parts = listOf(
                    Part(
                        functionCall = FunctionCall(
                            name = toolName,
                            args = JsonObject(argumentsSupplied)
                        )
                    ),
                ),
                role = "model"
            ),
            result?.let {
                Content(
                    parts = listOf(
                        Part(
                            functionResponse = FunctionResponse(
                                name = toolName,
                                response = Response(
                                    content = listOf(TextResponse(it.text)),
                                    isError = it.isError,
                                )
                            )
                        )
                    ),
                    role = "model",
                )
            }
        )
    }
}

internal fun List<Tool>.toGeminiTools(): Tools {
    return Tools(
        functionDeclarations = this.map { tool ->
            FunctionDeclaration(
                name = tool.toolId,
                description = tool.description ?: "",
                parameters = Parameters(
                    properties = tool.argumentsSchema.propertiesSchema as JsonObject,
                    required = tool.argumentsSchema.requiredProperties
                )
            )
        }
    )
}

internal fun GenerateContentResponse.toConversationResponse(): ConversationResponse {
    val aiMessages = candidates.flatMap { candidate ->
        candidate.content.parts.mapNotNull { part ->
            when {
                part.text != null -> NetworkMessage.AiNetworkMessage.Text(part.text)
                part.functionCall != null -> NetworkMessage.AiNetworkMessage.ToolUse(
                    toolName = part.functionCall.name,
                    argumentsSupplied = part.functionCall.args
                )
                else -> null
            }
        }
    }

    return ConversationResponse(newMessages = aiMessages)
}