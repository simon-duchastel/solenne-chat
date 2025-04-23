package com.duchastel.simon.solenne.data.tools

import app.cash.turbine.test
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respondOk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFailsWith

internal class McpRepositoryImplTest {

    private val testScope: TestScope = TestScope()

    private lateinit var repo: McpRepositoryImpl

    @BeforeTest
    fun setup() {
        val client = HttpClient(MockEngine) {
            engine {
                addHandler { respondOk() }
            }
        }
        repo = McpRepositoryImpl(
            ioCoroutineScope = testScope,
            httpClient = client,
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
    fun `serverStatusFlow - emits after addServer`() = runTest {
        // add a server
        val conn = McpServer.Connection.Stdio("cmd")
        val server = repo.addServer("server1", conn)

        repo.serverStatusFlow().test {
            val statuses = awaitItem()
            assertEquals(1, statuses.size)
            assertEquals(server, statuses[0].mcpServer)
            assertTrue(statuses[0].status is McpServerStatus.Status.Offline)
            assertTrue(statuses[0].tools.isEmpty())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `addServer returns server`() = testScope.runTest {
        val conn = McpServer.Connection.Sse("url")
        val server = repo.addServer("srv", conn)
        assertEquals("srv", server.name)
        assertEquals(conn, server.connection)
        assertTrue(server.id.isNotBlank())
    }

    @Test
    fun `loadToolsForServer not connected returns empty`() = testScope.runTest {
        val server = repo.addServer("srv", McpServer.Connection.Sse("url"))
        val tools = repo.loadToolsForServer(server)
        assertTrue(tools.isEmpty())
    }

    @Test
    fun `callTool not connected throws`() = testScope.runTest {
        val server = repo.addServer("srv", McpServer.Connection.Sse("url"))
        assertFailsWith<IllegalStateException> {
            repo.callTool(server, "toolId", emptyMap())
        }
    }

    @Test
    fun `connect with Stdio does not throw`() = testScope.runTest {
        val server = repo.addServer("srv", McpServer.Connection.Stdio("cmd"))
        repo.connect(server)
        val statuses = repo.serverStatusFlow().first()
        assertTrue(statuses.isNotEmpty())
        val status = statuses.first()
        assertEquals(McpServerStatus.Status.Offline, status.status)
    }

    @Test
    fun `disconnect without connect does not throw`() = testScope.runTest {
        val server = repo.addServer("srv", McpServer.Connection.Sse("url"))
        repo.disconnect(server)
    }
}