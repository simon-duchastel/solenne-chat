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
import com.duchastel.simon.solenne.network.ai.Conversation
import com.duchastel.simon.solenne.network.ai.ConversationResponse
import com.duchastel.simon.solenne.network.ai.Message
import com.duchastel.simon.solenne.ui.model.toUIChatMessage
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonPrimitive
import com.duchastel.simon.solenne.network.ai.Tool as NetworkTool

class AiChatRepositoryImpl @Inject constructor(
    private val chatMessageRepository: ChatMessageRepository,
    private val mcpRepository: McpRepository,
    private val geminiApi: AiChatApi<GeminiModelScope>,
) : AiChatRepository {

    override fun messageFlowForConversation(
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
            chatMessageRepository.addTextMessageToConversation(
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
        toolResponse: Message.AiMessage.AiToolUse? = null,
    ) {
        val chatMessages =
            chatMessageRepository.getMessageFlowForConversation(conversationId).first()

        // Convert chat messages to conversation messages
        val conversationMessages = chatMessages.mapNotNull { message ->
            when (message.author) {
                is MessageAuthor.User -> Message.UserMessage(message.toUIChatMessage())
                is MessageAuthor.AI -> Message.AiMessage.AiTextMessage(message.text)
            }
        } + if (toolResponse != null) {
            listOf(toolResponse)
        } else {
            emptyList()
        }

        val conversation = Conversation(messages = conversationMessages)

        var messageId: String? = null
        var responseSoFar = ""

        var toolCallResult: CallToolResult? = null
        var toolCallName: String? = null
        var toolCall: Message.AiMessage.AiToolUse? = null

        when (aiModelScope) {
            is GeminiModelScope -> {
                geminiApi.generateStreamingResponseForConversation(
                    scope = aiModelScope,
                    conversation = conversation,
                    tools = toolsFlow.first().toAiTools(),
                )
            }
        }.mapNotNull { it() } // filter out nulls, ie failures
            .collect { response: ConversationResponse ->
                val aiMessages = response.newMessages

                for (message in aiMessages) {
                    when (message) {
                        is Message.AiMessage.AiTextMessage -> {
                            responseSoFar += message.text
                            val currentMessageId = messageId
                            if (currentMessageId == null) {
                                messageId = chatMessageRepository.addTextMessageToConversation(
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
                        }

                        is Message.AiMessage.AiToolUse -> {
                            // Handle tool use
                            toolCall = message
                            val serverTools = toolsFlow.first()
                            val (serverToCall, toolToCall) = serverTools[message.toolId]
                                ?: error("Server no longer available")

                            val toolResultMessageId = chatMessageRepository.addTextMessageToConversation(
                                conversationId = conversationId,
                                author = MessageAuthor.System,
                                text = "Call to ${toolToCall.name} with arguments: ${message.argumentsSupplied}",
                            )

                            toolCallResult = mcpRepository.callTool(
                                server = serverToCall.mcpServer,
                                tool = toolToCall,
                                arguments = message.argumentsSupplied,
                            )
                            toolCallName = toolToCall.name
                            messageId = null

                            chatMessageRepository.modifyMessageFromConversation(
                                conversationId = conversationId,
                                messageId = toolResultMessageId,
                                newText = "Call to ${toolToCall.name} with arguments: ${message.argumentsSupplied}" +
                                        "\n" + if (toolCallResult!!.isError) "Failed" else "Succeeded" +
                                        "\nResult: ${toolCallResult!!.text}",
                            )
                        }
                    }
                }
            }

            if (toolCallResult != null && toolCall != null) {
                val toolResponseMessage = Message.AiMessage.AiToolUse(
                    toolId = toolCallName!!,
                    argumentsSupplied = mapOf(
                        "isError" to JsonPrimitive(toolCallResult!!.isError),
                        "text" to JsonPrimitive(toolCallResult!!.text),
                    ),
                )

                generateStreamingResponse(
                    aiModelScope = aiModelScope,
                    conversationId = conversationId,
                    toolResponse = toolResponseMessage,
                )
            }
        }

    /**
     * Helper flow to get all of the tools which are currently available as
     * a map of tool name -> (server, tool) pair
     */
    private val toolsFlow = mcpRepository.serverStatusFlow()
        .distinctUntilChanged()
        .map { servers ->
            servers
                .filter { it.status is McpServerStatus.Status.Connected }
                .flatMap { server ->
                    server.tools.map { tool ->
                        // avoid collisions among tool name by appending the tool-name with
                        // the first 4 characters of the server id, which is guaranteed
                        // to be unique across servers.
                        "${tool.name}-${server.mcpServer.id.take(4)}" to (server to tool)
                    }
                }
                .toMap()
        }.distinctUntilChanged()

    /**
     * Helper function to transform the map of tools into a list of [NetworkTool]s that
     * can be passed to the AI model.
     */
    private inline fun Map<String, Pair<McpServerStatus, Tool>>.toAiTools(): List<NetworkTool> {
        return map { entry ->
            val functionName = entry.key
            val (_, tool) = entry.value
            NetworkTool(
                toolId = functionName,
                description = tool.description,
                argumentsSchema = NetworkTool.ArgumentsSchema(
                    propertiesSchema = tool.argumentsSchema,
                    requiredProperties = tool.requiredArguments,
                )
            )
        }
    }
}