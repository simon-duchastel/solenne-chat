package com.duchastel.simon.solenne.network.ai.gemini

import com.duchastel.simon.solenne.network.ai.Conversation
import com.duchastel.simon.solenne.network.ai.NetworkMessage
import com.duchastel.simon.solenne.network.ai.Tool
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class GeminiApiTest {

    @Test
    fun `test Message UserMessage toContent`() {
        // Given
        val userNetworkMessage = NetworkMessage.UserNetworkMessage("Hello, how are you?")

        // When
        val content = userNetworkMessage.toContent()

        // Then
        assertEquals("user", content.role)
        assertEquals(1, content.parts.size)
        assertEquals("Hello, how are you?", content.parts[0].text)
        assertNull(content.parts[0].functionCall)
    }

    @Test
    fun `test Message AiTextMessage toContent`() {
        // Given
        val aiNetworkMessage = NetworkMessage.AiNetworkMessage.Text("I'm doing well, thanks for asking!")

        // When
        val content = aiNetworkMessage.toContent()

        // Then
        assertEquals("model", content.role)
        assertEquals(1, content.parts.size)
        assertEquals("I'm doing well, thanks for asking!", content.parts[0].text)
        assertNull(content.parts[0].functionCall)
    }

    @Test
    fun `test Message AiToolUse toContent`() {
        // Given
        val arguments = mapOf(
            "query" to JsonPrimitive("weather in San Francisco"),
            "includeDetails" to JsonPrimitive(true)
        )
        val toolUse = NetworkMessage.AiNetworkMessage.ToolUse(
            toolId = "search_weather",
            argumentsSupplied = arguments
        )

        // When
        val content = toolUse.toContent()

        // Then
        assertEquals("model", content.role)
        assertEquals(1, content.parts.size)
        assertNull(content.parts[0].text)
        assertNotNull(content.parts[0].functionCall)
        assertEquals("search_weather", content.parts[0].functionCall?.name)
        assertEquals(
            JsonPrimitive("weather in San Francisco"),
            content.parts[0].functionCall?.args?.get("query")
        )
        assertEquals(
            JsonPrimitive(true),
            content.parts[0].functionCall?.args?.get("includeDetails")
        )
    }

    @Test
    fun `test List Tool toGeminiTools`() {
        // Given
        val propertiesSchema = JsonObject(
            mapOf(
            "query" to JsonPrimitive("string"),
            "includeDetails" to JsonPrimitive(true)
        )
        )
        val tools = listOf(
            Tool(
                toolId = "search_weather",
                description = "Search for weather information",
                argumentsSchema = Tool.ArgumentsSchema(
                    propertiesSchema = propertiesSchema,
                    requiredProperties = listOf("query")
                )
            ),
            Tool(
                toolId = "get_time",
                description = "Get current time",
                argumentsSchema = Tool.ArgumentsSchema(
                    propertiesSchema = JsonObject(mapOf("timezone" to JsonPrimitive("string"))),
                    requiredProperties = emptyList()
                )
            )
        )

        // When
        val geminiTools = tools.toGeminiTools()

        // Then
        assertEquals(2, geminiTools.functionDeclarations?.size)

        val weatherTool = geminiTools.functionDeclarations?.get(0)
        assertEquals("search_weather", weatherTool?.name)
        assertEquals("Search for weather information", weatherTool?.description)
        assertEquals(JsonPrimitive("string"), weatherTool?.parameters?.properties?.get("query"))
        assertEquals(
            JsonPrimitive(true),
            weatherTool?.parameters?.properties?.get("includeDetails")
        )
        assertEquals(listOf("query"), weatherTool?.parameters?.required)

        val timeTool = geminiTools.functionDeclarations?.get(1)
        assertEquals("get_time", timeTool?.name)
        assertEquals("Get current time", timeTool?.description)
        assertEquals(JsonPrimitive("string"), timeTool?.parameters?.properties?.get("timezone"))
        assertEquals(emptyList(), timeTool?.parameters?.required)
    }

    @Test
    fun `test createGenerateContentRequest with no tools and no system prompt`() {
        // Given
        val conversation = Conversation(
            networkMessages = listOf(
                NetworkMessage.UserNetworkMessage("Hello, AI!")
            )
        )

        // When
        val request = createGenerateContentRequest(
            conversation = conversation,
            systemPrompt = null,
            tools = emptyList()
        )

        // Then
        assertEquals(1, request.contents.size)
        assertEquals("user", request.contents[0].role)
        assertEquals("Hello, AI!", request.contents[0].parts[0].text)
        assertNull(request.tools)
        assertNull(request.systemInstruction)
    }

    @Test
    fun `test createGenerateContentRequest with system prompt`() {
        // Given
        val conversation = Conversation(
            networkMessages = listOf(
                NetworkMessage.UserNetworkMessage("Hello, AI!")
            )
        )
        val systemPrompt = "You are a helpful assistant."

        // When
        val request = createGenerateContentRequest(
            conversation = conversation,
            systemPrompt = systemPrompt,
            tools = emptyList()
        )

        // Then
        assertEquals(1, request.contents.size)
        assertEquals("user", request.contents[0].role)
        assertEquals("Hello, AI!", request.contents[0].parts[0].text)
        assertNull(request.tools)
        assertNotNull(request.systemInstruction)
        assertEquals(systemPrompt, request.systemInstruction?.parts?.get(0)?.text)
    }

    @Test
    fun `test createGenerateContentRequest with tools`() {
        // Given
        val conversation = Conversation(
            networkMessages = listOf(
                NetworkMessage.UserNetworkMessage("What's the weather?")
            )
        )
        val tools = listOf(
            Tool(
                toolId = "search_weather",
                description = "Search for weather information",
                argumentsSchema = Tool.ArgumentsSchema(
                    propertiesSchema = JsonObject(mapOf("location" to JsonPrimitive("string"))),
                    requiredProperties = listOf("location")
                )
            )
        )

        // When
        val request = createGenerateContentRequest(
            conversation = conversation,
            systemPrompt = null,
            tools = tools
        )

        // Then
        assertEquals(1, request.contents.size)
        assertEquals("user", request.contents[0].role)
        assertEquals("What's the weather?", request.contents[0].parts[0].text)
        assertNotNull(request.tools)
        assertEquals(1, request.tools?.size)
        assertEquals(1, request.tools?.get(0)?.functionDeclarations?.size)
        assertEquals("search_weather", request.tools?.get(0)?.functionDeclarations?.get(0)?.name)
    }

    @Test
    fun `test GenerateContentResponse toConversationResponse with text response`() {
        // Given
        val response = GenerateContentResponse(
            candidates = listOf(
                Candidate(
                    content = Content(
                        parts = listOf(
                            Part(text = "I'm an AI assistant.")
                        )
                    ),
                    finishReason = "STOP"
                )
            )
        )

        // When
        val conversationResponse = response.toConversationResponse()

        // Then
        assertEquals(1, conversationResponse.newMessages.size)
        val message = conversationResponse.newMessages[0]
        assertTrue(message is NetworkMessage.AiNetworkMessage.Text)
        assertEquals("I'm an AI assistant.", (message as NetworkMessage.AiNetworkMessage.Text).text)
    }

    @Test
    fun `test GenerateContentResponse toConversationResponse with function call`() {
        // Given
        val functionCall = FunctionCall(
            name = "search_weather",
            args = JsonObject(mapOf("location" to JsonPrimitive("New York")))
        )
        val response = GenerateContentResponse(
            candidates = listOf(
                Candidate(
                    content = Content(
                        parts = listOf(
                            Part(functionCall = functionCall)
                        )
                    )
                )
            )
        )

        // When
        val conversationResponse = response.toConversationResponse()

        // Then
        assertEquals(1, conversationResponse.newMessages.size)
        val message = conversationResponse.newMessages[0]
        assertTrue(message is NetworkMessage.AiNetworkMessage.ToolUse)
        val toolUse = message as NetworkMessage.AiNetworkMessage.ToolUse
        assertEquals("search_weather", toolUse.toolId)
        assertEquals(JsonPrimitive("New York"), toolUse.argumentsSupplied["location"])
    }

    @Test
    fun `test GenerateContentResponse toConversationResponse with multiple parts`() {
        // Given
        val response = GenerateContentResponse(
            candidates = listOf(
                Candidate(
                    content = Content(
                        parts = listOf(
                            Part(text = "Here's the weather:"),
                            Part(
                                functionCall = FunctionCall(
                                    name = "search_weather",
                                    args = JsonObject(mapOf("location" to JsonPrimitive("New York")))
                                )
                            )
                        )
                    )
                )
            )
        )

        // When
        val conversationResponse = response.toConversationResponse()

        // Then
        assertEquals(2, conversationResponse.newMessages.size)

        val textMessage = conversationResponse.newMessages[0]
        assertTrue(textMessage is NetworkMessage.AiNetworkMessage.Text)
        assertEquals("Here's the weather:", (textMessage as NetworkMessage.AiNetworkMessage.Text).text)

        val toolUseMessage = conversationResponse.newMessages[1]
        assertTrue(toolUseMessage is NetworkMessage.AiNetworkMessage.ToolUse)
        assertEquals("search_weather", (toolUseMessage).toolId)
        assertEquals(
            JsonPrimitive("New York"),
            (toolUseMessage).argumentsSupplied["location"]
        )
    }

    @Test
    fun `test GenerateContentResponse toConversationResponse with empty or null parts`() {
        // Given
        val response = GenerateContentResponse(
            candidates = listOf(
                Candidate(
                    content = Content(
                        parts = listOf(
                            Part() // Empty part with no text or function call
                        )
                    )
                )
            )
        )

        // When
        val conversationResponse = response.toConversationResponse()

        // Then
        assertEquals(0, conversationResponse.newMessages.size)
    }
}