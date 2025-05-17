package com.duchastel.simon.solenne.data.ai

import com.duchastel.simon.solenne.data.ai.AIModelScope.GeminiModelScope
import com.duchastel.simon.solenne.data.chat.ChatMessageRepository
import com.duchastel.simon.solenne.data.chat.models.ChatMessage
import com.duchastel.simon.solenne.data.chat.models.ChatMessage.ToolUse
import com.duchastel.simon.solenne.data.chat.models.MessageAuthor
import com.duchastel.simon.solenne.data.tools.McpRepository
import com.duchastel.simon.solenne.data.tools.McpServerStatus
import com.duchastel.simon.solenne.data.tools.Tool
import com.duchastel.simon.solenne.db.aimodelscope.AIApiKeyDb
import com.duchastel.simon.solenne.dispatchers.IODispatcher
import com.duchastel.simon.solenne.network.ai.AiChatApi
import com.duchastel.simon.solenne.network.ai.Conversation
import com.duchastel.simon.solenne.network.ai.ConversationResponse
import com.duchastel.simon.solenne.network.ai.NetworkMessage
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.withContext
import com.duchastel.simon.solenne.network.ai.Tool as NetworkTool

class AiChatRepositoryImpl @Inject constructor(
    private val aiApiKeyDb: AIApiKeyDb,
    private val chatMessageRepository: ChatMessageRepository,
    private val mcpRepository: McpRepository,
    private val geminiApi: AiChatApi<GeminiModelScope>,
) : AiChatRepository {

    override fun getAvailableModelsFlow(): Flow<List<AIModelProviderStatus<*>>> {
        return aiApiKeyDb.getGeminiApiKeyFlow()
            .map { apiKey ->
                if (apiKey == null) null else GeminiModelScope(apiKey)
            }
            .map(GeminiModelScope?::toGeminiModelProviderStatus)
            .map(::listOf)
    }

    @Suppress("UNCHECKED_CAST")
    override suspend fun <T : AIModelProvider> configureModel(
        config: AIProviderConfig<T>,
    ): AIModelProviderStatus<T>? {
        when (config) {
            is AIProviderConfig.GeminiConfig -> {
                val apiKey = aiApiKeyDb.saveGeminiApiKey(config.apiKey) ?: return null
                val geminiModelStatus = AIModelProviderStatus.Gemini(GeminiModelScope(apiKey))
                return geminiModelStatus as AIModelProviderStatus<T>
            }
        }
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
    ) {
        // Fetch existing chat messages and convert them to be sent over the network
        val chatMessages =
            chatMessageRepository.getMessageFlowForConversation(conversationId).first()
        val conversationNetworkMessages = chatMessages.map(ChatMessage::toAiNetworkMessage)

        var messageBeingProcessed: ChatMessage? = null
        val conversation = Conversation(networkMessages = conversationNetworkMessages)
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
                        is NetworkMessage.AiNetworkMessage.Text -> {
                            // reset the message if we're switching types
                            if (messageBeingProcessed !is ChatMessage.Text) {
                                messageBeingProcessed = null
                            }

                            val existingMessage = messageBeingProcessed
                            messageBeingProcessed = when (existingMessage) {
                                null -> {
                                    chatMessageRepository.addTextMessageToConversation(
                                        conversationId = conversationId,
                                        author = MessageAuthor.AI,
                                        text = message.text,
                                    )
                                }
                                is ChatMessage.Text -> {
                                    chatMessageRepository.modifyMessageFromConversation(
                                        conversationId = conversationId,
                                        messageId = existingMessage.id,
                                        updatedText = existingMessage.text + message.text,
                                    )
                                }
                                !is ChatMessage.Text -> {
                                    // unexpected error - chat message unexpectedly changed types
                                    // this should be impossible
                                    return@collect
                                }
                            }
                        }
                        is NetworkMessage.AiNetworkMessage.ToolUse -> {
                            // TODO: gracefully handle nulls
                            val serverTools = toolsFlow.first()
                            val (serverToCall, toolToCall) = serverTools[message.toolName]
                                ?: run {
                                    return@collect // if null, tool is no longer available on server
                                }

                            val toolUseMessage = chatMessageRepository.addToolUseToConversation(
                                conversationId = conversationId,
                                mcpServer = serverToCall.mcpServer,
                                toolName = toolToCall.name,
                                argumentsSupplied = message.argumentsSupplied,
                            ) ?: return@collect // if null, we had an error adding the tool message

                            val callToolResult = mcpRepository.callTool(
                                server = serverToCall.mcpServer,
                                tool = toolToCall,
                                arguments = message.argumentsSupplied,
                            ) ?: return@collect // if null, we had an error calling the tool

                            messageBeingProcessed = chatMessageRepository.addToolUseResultToConversation(
                                conversationId = conversationId,
                                messageId = toolUseMessage.id,
                                toolResult = ToolUse.ToolResult(
                                    text = callToolResult.text,
                                    isError = callToolResult.isError,
                                )
                            )
                        }
                    }
                }
            }

        // if the last message processed the AI using a tool, generate another set of messages
        // to give the AI a chance to respond to the tool's use
        if (messageBeingProcessed is ToolUse) {
            generateStreamingResponse(aiModelScope = aiModelScope, conversationId = conversationId)
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
                        // Warning: this has a risk of collisions since two MCP servers
                        // can have the same tool name (in theory)
                        tool.name to (server to tool)
                    }
                }
                .toMap()
        }.distinctUntilChanged()
}

/**
 * Helper function to transform the map of tools into a list of [NetworkTool]s that
 * can be passed to the AI model.
 */
private fun Map<String, Pair<McpServerStatus, Tool>>.toAiTools(): List<NetworkTool> {
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

/**
 * Helper function to transform a [ChatMessage] into a [NetworkMessage] that
 * can be passed to the AI model.
 */
private fun ChatMessage.toAiNetworkMessage(): NetworkMessage {
    return when (this) {
        is ChatMessage.Text -> {
            when (author) {
                is MessageAuthor.User -> NetworkMessage.UserNetworkMessage(text)
                is MessageAuthor.AI -> NetworkMessage.AiNetworkMessage.Text(text)
            }
        }
        is ToolUse -> {
            NetworkMessage.AiNetworkMessage.ToolUse(
                toolName = toolName,
                argumentsSupplied = argumentsSupplied,
                result = result?.let {
                    NetworkMessage.AiNetworkMessage.ToolUse.ToolResult(
                        text = it.text,
                        isError = it.isError
                    )
                }
            )
        }
    }
}

/**
 * Converts a [GeminiModelScope] to a [AIModelProviderStatus.Gemini].
 * If null is provided, a status with a null scope is used.
 */
private fun GeminiModelScope?.toGeminiModelProviderStatus(): AIModelProviderStatus.Gemini {
    return AIModelProviderStatus.Gemini(this)
}