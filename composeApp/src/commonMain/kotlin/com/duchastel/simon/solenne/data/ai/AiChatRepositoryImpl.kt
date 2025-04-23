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
import com.duchastel.simon.solenne.network.ai.FunctionCall
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

            generateStreamingResponse(
                aiModelScope = aiModelScope,
                conversationId = conversationId,
            )
        }
    }

    private suspend fun generateStreamingResponse(
        aiModelScope: AIModelScope,
        conversationId: String,
        functionResponse: FunctionResponse? = null,
        functionCall: FunctionCall? = null,
    ) {
        val conversationContents = chatMessageRepository.getMessageFlowForConversation(conversationId)
            .first()
            .mapNotNull { message ->
                when (message.author) {
                    is MessageAuthor.System -> return@mapNotNull null
                    is MessageAuthor.User -> {
                        Content(
                            parts = listOf(Part(message.text)),
                            role = "user"
                        )
                    }
                    is MessageAuthor.AI -> {
                        Content(
                            parts = listOf(Part(message.text)),
                            role = "model"
                        )
                    }
                }
            } + if (functionResponse != null && functionCall != null) {
                listOf(
                    Content(
                        role = "model",
                        parts = listOf(Part(functionCall = functionCall))
                    ),
                    Content(
                        role = "user",
                        parts = listOf(Part(functionResponse = functionResponse))
                    ),
                )
            } else {
                emptyList()
            }

        var messageId: String? = null
        var responseSoFar = ""

        var toolCallResult: CallToolResult? = null
        var toolCallName: String? = null
        var requestedFunctionCall: FunctionCall? = null
        when (aiModelScope) {
            is GeminiModelScope -> {
                geminiApi.generateStreamingResponseForConversation(
                    scope = aiModelScope,
                    request = GenerateContentRequest(
                        contents = conversationContents,
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
                requestedFunctionCall = response.functionCall
                    ?: error("Error: function call expected, $response found")
                val serverTools = toolsFlow.first()
                val (serverToCall, toolToCall) = serverTools[requestedFunctionCall!!.name] ?: error("Server no longer available")

                val toolResultMessageId = chatMessageRepository.addMessageToConversation(
                    conversationId = conversationId,
                    author = MessageAuthor.System,
                    text = "Call to ${toolToCall.name} with arguments: ${requestedFunctionCall!!.args}",
                )

                toolCallResult = mcpRepository.callTool(
                    server = serverToCall.mcpServer,
                    tool = toolToCall,
                    arguments = requestedFunctionCall!!.args ?: emptyMap(),
                )
                toolCallName = toolToCall.name
                messageId = null

                chatMessageRepository.modifyMessageFromConversation(
                    conversationId = conversationId,
                    messageId = toolResultMessageId,
                    newText = "Call to ${toolToCall.name} with arguments: ${requestedFunctionCall!!.args}" +
                            "\n" + if (toolCallResult!!.isError) "Failed" else "Succeeded" +
                            "\nResult: ${toolCallResult!!.text}",
                )
            }
        }

        if (toolCallResult != null) {
            generateStreamingResponse(
                aiModelScope = aiModelScope,
                conversationId = conversationId,
                functionResponse = FunctionResponse(
                    name = toolCallName!!,
                    response = Response(
                        isError = toolCallResult!!.isError,
                        content = listOf(TextResponse(toolCallResult!!.text)),
                    )
                ),
                functionCall = requestedFunctionCall!!,
            )
        }
    }
}