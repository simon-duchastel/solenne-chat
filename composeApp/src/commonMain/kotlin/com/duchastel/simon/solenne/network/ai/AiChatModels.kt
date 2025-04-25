package com.duchastel.simon.solenne.network.ai

import com.duchastel.simon.solenne.network.ai.Message.AiMessage
import kotlinx.serialization.json.JsonElement

data class Conversation(
    val messages: List<Message>,
)

sealed interface Message {
    data class UserMessage(val text: String) : Message

    sealed interface AiMessage : Message {
        data class AiTextMessage(val text: String) : AiMessage
        data class AiToolUse(
            val toolId: String,
            val argumentsSupplied: Map<String, JsonElement?>,
        ) : AiMessage
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
    val newMessages: List<AiMessage>,
)