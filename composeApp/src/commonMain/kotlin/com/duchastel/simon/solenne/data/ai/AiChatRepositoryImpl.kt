package com.duchastel.simon.solenne.data.ai

import com.duchastel.simon.solenne.data.ai.AIModelScope.GeminiModelScope
import com.duchastel.simon.solenne.data.chat.ChatMessage
import com.duchastel.simon.solenne.data.chat.ChatMessageRepository
import com.duchastel.simon.solenne.data.chat.MessageAuthor
import com.duchastel.simon.solenne.data.tools.CallToolResult
import com.duchastel.simon.solenne.data.tools.McpRepository
import com.duchastel.simon.solenne.data.tools.McpServerStatus
import com.duchastel.simon.solenne.data.tools.Tool
import com.duchastel.simon.solenne.dispatchers.IODispatcher
import com.duchastel.simon.solenne.network.ai.AiChatApi
import com.duchastel.simon.solenne.network.ai.Content
import com.duchastel.simon.solenne.network.ai.FunctionDeclaration
import com.duchastel.simon.solenne.network.ai.FunctionResponse
import com.duchastel.simon.solenne.network.ai.GenerateContentRequest
import com.duchastel.simon.solenne.network.ai.Parameters
import com.duchastel.simon.solenne.network.ai.Part
import com.duchastel.simon.solenne.network.ai.Response
import com.duchastel.simon.solenne.network.ai.TextResponse
import com.duchastel.simon.solenne.network.ai.Tools
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonObject

class AiChatRepositoryImpl @Inject constructor(
    private val chatMessageRepository: ChatMessageRepository,
    private val mcpRepository: McpRepository,
    private val geminiApi: AiChatApi<GeminiModelScope>,
) : AiChatRepository {
    
    private val toolsFlow = mcpRepository.serverStatusFlow()
        .distinctUntilChanged()
        .map { servers ->
            servers
                .filter { it.status is McpServerStatus.Status.Connected }
                .flatMap { server ->
                    server.tools.map { tool ->
                        "${tool.name}-${server.mcpServer.id}" to (server to tool)
                    }
                }
                .toMap()
        }.distinctUntilChanged()

    private fun Map<String, Pair<McpServerStatus, Tool>>.toFunctionDeclarations(): Tools? {
        return Tools(
            functionDeclarations = map { entry ->
                val functionName = entry.key
                val (_, tool) = entry.value
                FunctionDeclaration(
                    name = functionName,
                    description = tool.description ?: "No description",
                    parameters = Parameters(
                        properties = tool.parameters,
                        required = tool.requiredParameters,
                    ),
                )
            }.ifEmpty { return null }, // an empty functionDeclarations array is invalid
        )
    }

    override fun getMessageFlowForConversation(
        conversationId: String
    ): Flow<List<ChatMessage>> {
        return chatMessageRepository.getMessageFlowForConversation(conversationId)
    }

    override suspend fun sendTextMessageFromUserToConversation(
        aiModelScope: AIModelScope,
        conversationId: String,
        text: String,
    ) {
        withContext(IODispatcher) {
            chatMessageRepository.addMessageToConversation(
                conversationId = conversationId,
                author = MessageAuthor.User,
                text = text,
            )

            var conversationContents = chatMessageRepository.getMessageFlowForConversation(conversationId)
                .first()
                .map { message ->
                    Content(
                        parts = listOf(Part(message.text)),
                        role = when (message.author) {
                            is MessageAuthor.User -> "user"
                            is MessageAuthor.AI -> "model"
                        },
                    )
                }
            do {
                println("TODO - $conversationContents")
                conversationContents = generateStreamingResponse(
                    aiModelScope = aiModelScope,
                    conversationId = conversationId,
                    contents = conversationContents,
                )
            } while (conversationContents.last().parts.find { it.functionResponse != null } != null)
        }
    }

    private suspend fun generateStreamingResponse(
        aiModelScope: AIModelScope,
        conversationId: String,
        contents: List<Content>,
    ): List<Content> {
        var messageId: String? = null
        var responseSoFar = ""

        var toolCallResult: CallToolResult? = null
        var toolCallName: String? = null
        when (aiModelScope) {
            is GeminiModelScope -> {
                geminiApi.generateStreamingResponseForConversation(
                    scope = aiModelScope,
                    request = GenerateContentRequest(
                        contents = contents,
                        tools = toolsFlow.first().toFunctionDeclarations()
                            ?.let { listOf(it) }
                            ?: emptyList(),
                    )
                )
            }
        }.collect {
            val response = it.candidates
                .firstOrNull()?.content?.parts?.firstOrNull()
                ?: error("Error: No response from AI")
            if (response.text != null) {
                responseSoFar += response.text
                val currentMessageId = messageId
                if (currentMessageId == null) {
                    messageId = chatMessageRepository.addMessageToConversation(
                        conversationId = conversationId,
                        author = MessageAuthor.AI,
                        text = responseSoFar,
                    )
                } else {
                    chatMessageRepository.modifyMessageFromConversation(
                        messageId = currentMessageId,
                        conversationId = conversationId,
                        newText = responseSoFar,
                    )
                }
            } else  {
                // if it's not a text response, assume it's a function call
                val functionCall = response.functionCall
                    ?: error("Error: function call expected, $response found")
                val serverTools = toolsFlow.first()
                val (serverToCall, toolToCall) = serverTools[functionCall.name] ?: error("Server no longer available")

                val toolResultMessageId = chatMessageRepository.addMessageToConversation(
                    conversationId = conversationId,
                    author = MessageAuthor.AI,
                    text = "Call to ${toolToCall.name} with arguments: ${functionCall.args}",
                )

                toolCallResult = mcpRepository.callTool(
                    server = serverToCall.mcpServer,
                    tool = toolToCall,
                    arguments = functionCall.args ?: emptyMap(),
                )
                toolCallName = toolToCall.name
                messageId = null

                chatMessageRepository.modifyMessageFromConversation(
                    conversationId = conversationId,
                    messageId = toolResultMessageId,
                    newText = "Call to ${toolToCall.name} with arguments: ${functionCall.args}" +
                            "\n" + if (toolCallResult!!.isError) "Failed" else "Succeeded" +
                            "\nResult: ${toolCallResult!!.text}",
                )

            }
        }
        return contents + if (toolCallResult != null) {
            listOf(Content(
                role = "user",
                parts = listOf(Part(
                    functionResponse = FunctionResponse(
                        name = toolCallName!!,
                        response = Response(
                            isError = toolCallResult!!.isError,
                            content = listOf(TextResponse(toolCallResult!!.text)),
                        )
                    )
                ))
            ))
        } else {
            emptyList()
        }
    }
}