package com.duchastel.simon.solenne.data.tools

import app.cash.turbine.test
import com.duchastel.simon.solenne.util.fakes.FakeMcpToolsDb
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respondOk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

// Ignored because the MCP SDK doesn't have any fakes
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
            ioCoroutineScope = testScope,
            httpClient = client,
            mcpToolsDb = fakeMcpToolsDb,
        )
    }

    @Test
    fun `serverStatusFlow - initial empty`() = testScope.runTest {
        repo.serverStatusFlow().test {
            val first = awaitItem()
            assertTrue(first.isEmpty())
        }
    }

    @Test
    fun `serverStatusFlow - emits after addServer`() = testScope.runTest {
        // add a server
        val conn = McpServerConfig.Connection.Stdio("cmd")
        val server = repo.addServer("server1", conn)

        repo.serverStatusFlow().test {
            val statuses = awaitItem()
            assertEquals(1, statuses.size)
            assertEquals(server, statuses[0].config)
            assertTrue(statuses[0].status is McpServer.Status.Offline)
            assertTrue(statuses[0].tools.isEmpty())

            cancelAndIgnoreRemainingEvents()
        }
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
    fun `loadToolsForServer not connected returns empty`() = testScope.runTest {
        val server = repo.addServer("srv", McpServerConfig.Connection.Sse("url"))
        val tools = repo.loadToolsForServer(server)

        assertTrue(tools?.isEmpty() == true)
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
    fun `connect with Stdio does not throw`() = testScope.runTest {
        val server = repo.addServer("srv", McpServerConfig.Connection.Stdio("cmd"))

        repo.connect(server)
        val statuses = repo.serverStatusFlow().first()
        assertTrue(statuses.isNotEmpty())
        val status = statuses.first()
        assertEquals(McpServer.Status.Offline, status.status)
    }

    @Test
    fun `disconnect without connect does not throw`() = testScope.runTest {
        val server = repo.addServer("srv", McpServerConfig.Connection.Sse("url"))

        val result = repo.disconnect(server.id)

        // Should return status even if not connected
        assertEquals(server.id, result)
    }
}