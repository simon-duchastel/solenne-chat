package com.duchastel.simon.solenne.network.ai.gemini

import kotlinx.serialization.Required
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

@Serializable
data class GenerateContentRequest(
    @SerialName("contents") val contents: List<Content>,
    @SerialName("tools") val tools: List<Tools>? = null,
    @SerialName("systemInstruction") val systemInstruction: Content? = null,
)

@Serializable
data class Content(
    @SerialName("parts") val parts: List<Part>,
    @SerialName("role") val role: String? = null,
)

@Serializable
data class Part(
    @SerialName("text") val text: String? = null,
    @SerialName("functionCall") val functionCall: FunctionCall? = null,
    @SerialName("functionResponse") val functionResponse: FunctionResponse? = null,
)

@Serializable
data class GenerateContentResponse(
    @SerialName("candidates") val candidates: List<Candidate> = emptyList()
)

@Serializable
data class Candidate(
    @SerialName("content") val content: Content,
    @SerialName("finishReason") val finishReason: String? = null,
)

@Serializable
data class Tools(
    @SerialName("functionDeclarations") val functionDeclarations: List<FunctionDeclaration>? = null
)

@Serializable
data class FunctionDeclaration(
    @SerialName("name") val name: String,
    @SerialName("description") val description: String,
    @SerialName("parameters") val parameters: Parameters,
)

@Serializable
data class Parameters(
    @SerialName("properties") val properties: JsonObject,
    @SerialName("required") val required: List<String> = emptyList(),
) {
    @Required
    @SerialName("type") val type: String = "object"
}

@Serializable
data class FunctionCall(
    @SerialName("id") val id: String? = null,
    @SerialName("name") val name: String,
    @SerialName("args") val args: JsonObject = JsonObject(emptyMap()),
)

@Serializable
data class FunctionResponse(
    @SerialName("id") val id: String? = null,
    @SerialName("name") val name: String,
    @SerialName("response") val response: Response,
)

@Serializable
data class Response(
    @SerialName("content") val content: List<TextResponse>? = null,
    @SerialName("isError") val isError: Boolean,
)

@Serializable
data class TextResponse(
    @SerialName("text") val text: String,
) {
    @Required
    @SerialName("type") val type: String = "text"
}
