package com.duchastel.simon.solenne.tests.data.tools

import com.duchastel.simon.solenne.data.tools.McpRepositoryImpl
import com.duchastel.simon.solenne.data.tools.McpServer
import com.duchastel.simon.solenne.data.tools.McpServerConfig
import com.duchastel.simon.solenne.data.tools.Tool
import com.duchastel.simon.solenne.fakes.FakeMcpToolsDb
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respondOk
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

// TODO: add mocks or fakes for the MCP SDK
@Ignore
internal class McpRepositoryImplTest {

    private val testScope: TestScope = TestScope()
    private lateinit var fakeMcpToolsDb: FakeMcpToolsDb
    private lateinit var repo: McpRepositoryImpl

    @BeforeTest
    fun setup() {
        val client = HttpClient(MockEngine) {
            engine {
                addHandler { respondOk() }
            }
        }
        fakeMcpToolsDb = FakeMcpToolsDb()
        repo = McpRepositoryImpl(
            ioCoroutineScope = testScope.backgroundScope,
            httpClient = client,
            mcpToolsDb = fakeMcpToolsDb,
        )
    }

    @Test
    fun `addServer returns server`() = testScope.runTest {
        val conn = McpServerConfig.Connection.Sse("url")
        val server = repo.addServer("srv", conn)

        assertEquals("srv", server.name)
        assertEquals(conn, server.connection)
        assertTrue(server.id.isNotBlank())
    }

    @Test
    fun `addServer - creates server with unique id`() = testScope.runTest {
        val conn = McpServerConfig.Connection.Stdio("cmd")
        val server1 = repo.addServer("srv1", conn)
        val server2 = repo.addServer("srv2", conn)

        assertEquals("srv1", server1.name)
        assertEquals("srv2", server2.name)
        assertEquals(conn, server1.connection)
        assertEquals(conn, server2.connection)
        assertTrue(server1.id.isNotBlank())
        assertTrue(server2.id.isNotBlank())
        assertTrue(server1.id != server2.id)
    }

    @Test
    fun `loadToolsForServer - not connected returns null`() = testScope.runTest {
        val server = repo.addServer("srv", McpServerConfig.Connection.Sse("url"))
        val tools = repo.loadToolsForServer(server)

        assertNull(tools)
    }

    @Test
    fun `callTool not connected returns null`() = testScope.runTest {
        val server = repo.addServer("srv", McpServerConfig.Connection.Sse("url"))

        val tool = Tool(
            name = "test-tool",
            description = "Test tool",
            argumentsSchema = emptyMap(),
            requiredArguments = emptyList()
        )
        val result = repo.callTool(server, tool, emptyMap())
        assertNull(result)
    }

    @Test
    fun `callTool - with arguments`() = testScope.runTest {
        val server = repo.addServer("srv", McpServerConfig.Connection.Sse("url"))
        val tool = Tool(
            name = "test-tool",
            description = "Test tool with arguments",
            argumentsSchema = mapOf("param1" to buildJsonObject { put("type", "string") }),
            requiredArguments = listOf("param1")
        )
        val arguments = mapOf("param1" to JsonPrimitive("test-value"))

        val result = repo.callTool(server, tool, arguments)
        assertNull(result)
    }

    @Test
    fun `connect with SSE connection does not throw`() = testScope.runTest {
        val server =
            repo.addServer("srv", McpServerConfig.Connection.Sse("https://example.com/mcp"))

        // This will likely return null due to connection failure in test, but shouldn't throw
        repo.connect(server)
        // Test passes if no exception is thrown
    }

    @Test
    fun `connect with Stdio connection does not throw`() = testScope.runTest {
        val server = repo.addServer("srv", McpServerConfig.Connection.Stdio("echo hello"))

        // This will likely return null due to connection failure in test, but shouldn't throw
        repo.connect(server)
        // Test passes if no exception is thrown
    }

    @Test
    fun `disconnect without connect does not throw`() = testScope.runTest {
        val server = repo.addServer("srv", McpServerConfig.Connection.Sse("url"))

        val result = repo.disconnect(server.id)

        assertEquals(server.id, result)
    }

    @Test
    fun `disconnect - existing server returns server id`() = testScope.runTest {
        val server = repo.addServer("srv", McpServerConfig.Connection.Sse("url"))

        val result = repo.disconnect(server.id)

        assertEquals(server.id, result)
    }

    @Test
    fun `disconnect - non-existent server returns server id`() = testScope.runTest {
        val nonExistentId = "non-existent-id"

        val result = repo.disconnect(nonExistentId)

        assertEquals(nonExistentId, result)
    }

    @Test
    fun `tool creation - with required arguments`() = testScope.runTest {
        val tool = Tool(
            name = "complex-tool",
            description = "A tool with complex schema",
            argumentsSchema = mapOf(
                "required_param" to buildJsonObject { put("type", "string") },
                "optional_param" to buildJsonObject {
                    put("type", "number")
                    put("default", 42)
                }
            ),
            requiredArguments = listOf("required_param")
        )

        assertEquals("complex-tool", tool.name)
        assertEquals("A tool with complex schema", tool.description)
        assertEquals(1, tool.requiredArguments.size)
        assertEquals("required_param", tool.requiredArguments[0])
        assertEquals(2, tool.argumentsSchema.size)
    }
}
