package com.duchastel.simon.solenne.data.ai

import com.duchastel.simon.solenne.data.ai.AIModelScope.GeminiModelScope
import com.duchastel.simon.solenne.data.chat.ChatMessage
import com.duchastel.simon.solenne.data.chat.ChatMessageRepositoryImpl
import com.duchastel.simon.solenne.data.chat.MessageAuthor
import com.duchastel.simon.solenne.data.tools.McpRepository
import com.duchastel.simon.solenne.data.tools.McpRepositoryImpl
import com.duchastel.simon.solenne.data.tools.McpServerStatus
import com.duchastel.simon.solenne.data.tools.Tool
import com.duchastel.simon.solenne.dispatchers.IODispatcher
import com.duchastel.simon.solenne.network.ai.AiChatApi
import com.duchastel.simon.solenne.network.ai.Content
import com.duchastel.simon.solenne.network.ai.FunctionDeclaration
import com.duchastel.simon.solenne.network.ai.GenerateContentRequest
import com.duchastel.simon.solenne.network.ai.Part
import com.duchastel.simon.solenne.network.ai.Tools
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AiChatRepositoryImpl @Inject constructor(
    private val chatMessageRepositoryImpl: ChatMessageRepositoryImpl,
    private val mcpRepository: McpRepository,
    private val geminiApi: AiChatApi<GeminiModelScope>,
) : AiChatRepository {
    
    private val toolsFlow = mcpRepository.serverStatusFlow()
        .map { servers ->
            servers
                .flatMap { server ->
                    server.tools.map { tool ->
                        "${server.mcpServer.id}${tool.name}" to (server to tool)
                    }
                }
                .toMap()
        }

    private fun Map<String, Pair<McpServerStatus, Tool>>.toFunctionDeclarations(): Tools? {
        return Tools(
            functionDeclarations = map { entry ->
                val functionName = entry.key
                val (_, tool) = entry.value
                FunctionDeclaration(
                    name = functionName,
                    description = tool.description ?: "No description",
                    parameters = tool.parameters
                )
            }.ifEmpty { return null }, // an empty functionDeclarations array is invalid
        )
    }

    override fun getMessageFlowForConversation(
        conversationId: String
    ): Flow<List<ChatMessage>> {
        return chatMessageRepositoryImpl.getMessageFlowForConversation(conversationId)
    }

    override suspend fun sendTextMessageFromUserToConversation(
        aiModelScope: AIModelScope,
        conversationId: String,
        text: String,
    ) {
        withContext(IODispatcher) {
            chatMessageRepositoryImpl.addMessageToConversation(
                conversationId = conversationId,
                author = MessageAuthor.User,
                text = text,
            )

            val conversationContents = chatMessageRepositoryImpl.getMessageFlowForConversation(conversationId)
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

            var messageId: String? = null
            var responseSoFar = ""
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
                        messageId = chatMessageRepositoryImpl.addMessageToConversation(
                            conversationId = conversationId,
                            author = MessageAuthor.AI,
                            text = responseSoFar,
                        )
                    } else {
                        chatMessageRepositoryImpl.modifyMessageFromConversation(
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
                    chatMessageRepositoryImpl.addMessageToConversation(
                        conversationId = conversationId,
                        author = MessageAuthor.AI,
                        text = "Call to $serverToCall, $toolToCall, ${functionCall.arguments}",
                    )
//                    mcpRepository.callTool(
//                        server = serverToCall.mcpServer,
//                        tool = toolToCall,
//                        arguments = functionCall.arguments ?: emptyMap(),
//                    )
                    messageId = null
                }
            }
        }
    }
}