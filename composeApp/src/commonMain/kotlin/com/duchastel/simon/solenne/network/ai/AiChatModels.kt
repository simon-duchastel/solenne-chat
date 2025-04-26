package com.duchastel.simon.solenne.network.ai

import com.duchastel.simon.solenne.network.ai.NetworkMessage.AiNetworkMessage
import kotlinx.serialization.json.JsonElement

data class Conversation(
    val networkMessages: List<NetworkMessage>,
)

sealed interface NetworkMessage {
    data class UserNetworkMessage(val text: String) : NetworkMessage

    sealed interface AiNetworkMessage : NetworkMessage {
        data class Text(val text: String) : AiNetworkMessage

        data class ToolUse(
            val toolName: String,
            val argumentsSupplied: Map<String, JsonElement>,
            val result: ToolResult? = null,
        ) : AiNetworkMessage {

            data class ToolResult(
                val text: String,
                val isError: Boolean,
            )
        }
    }
}

data class Tool(
    val toolId: String,
    val description: String?,
    val argumentsSchema: ArgumentsSchema,
) {
    data class ArgumentsSchema(
        val propertiesSchema: Map<String, JsonElement>,
        val requiredProperties: List<String>,
    )
}

data class ConversationResponse(
    val newMessages: List<AiNetworkMessage>,
)