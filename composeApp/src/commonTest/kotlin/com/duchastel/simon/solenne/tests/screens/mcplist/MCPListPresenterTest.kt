package com.duchastel.simon.solenne.tests.screens.mcplist

import com.duchastel.simon.solenne.data.tools.McpServer
import com.duchastel.simon.solenne.data.tools.McpServerConfig
import com.duchastel.simon.solenne.fakes.FakeMcpRepository
import com.duchastel.simon.solenne.screens.mcplist.MCPListPresenter
import com.duchastel.simon.solenne.screens.mcplist.MCPListScreen
import com.duchastel.simon.solenne.screens.mcplist.UIMCPServer
import com.duchastel.simon.solenne.screens.mcplist.toUiModel
import com.slack.circuit.test.FakeNavigator
import com.slack.circuit.test.test
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class MCPListPresenterTest {

    private lateinit var navigator: FakeNavigator
    private lateinit var mcpRepository: FakeMcpRepository
    private lateinit var presenter: MCPListPresenter

    @BeforeTest
    fun setup() {
        navigator = FakeNavigator(MCPListScreen)
        mcpRepository = FakeMcpRepository()
        presenter = MCPListPresenter(
            navigator = navigator,
            mcpRepository = mcpRepository,
        )
    }

    @Test
    fun `present - emits list of MCP servers`() = runTest {
        mcpRepository.addServer(
            name = "Connected Server",
            connection = McpServerConfig.Connection.Stdio("test command"),
        ).apply {
            mcpRepository.connect(this!!) // connect this server to test the connected status
        }
        mcpRepository.addServer(
            name = "Disconnected Server",
            connection = McpServerConfig.Connection.Stdio("test command"),
        )

        presenter.test {
            val state = expectMostRecentItem()
            assertEquals(2, state.mcpServers.size)
            assertEquals("Connected Server", state.mcpServers[0].name)
            assertEquals(UIMCPServer.Status.Connected, state.mcpServers[0].status)
            assertEquals("Disconnected Server", state.mcpServers[1].name)
            assertEquals(UIMCPServer.Status.Disconnected, state.mcpServers[1].status)
        }
    }

    @Test
    fun `present - back press triggers navigation pop`() = runTest {
        presenter.test {
            val state = expectMostRecentItem()
            val eventSink = state.eventSink

            eventSink(MCPListScreen.Event.BackPressed)

            navigator.awaitPop()

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `present - connect to server calls repository connect`() = runTest {
        mcpRepository.addServer(
            name = "Test Server",
            connection = McpServerConfig.Connection.Stdio("test command"),
        )

        presenter.test {
            val state = expectMostRecentItem()
            val uiServer = state.mcpServers.first()

            state.eventSink(MCPListScreen.Event.ConnectToServer(uiServer))

            val updatedState = expectMostRecentItem()
            val updatedServer = updatedState.mcpServers.first()
            assertEquals(UIMCPServer.Status.Connected, updatedServer.status)
        }
    }

    @Test
    fun `toUiModel - correctly maps McpServerStatus to UIMCPServer`() = runTest {
        // Test Connected status
        val connectedServer = McpServerConfig(
            id = "test-id-1",
            name = "Connected Test Server",
            connection = McpServerConfig.Connection.Stdio("test command")
        )
        val connectedStatus = McpServer(
            config = connectedServer,
            status = McpServer.Status.Connected,
            tools = emptyList()
        )

        val connectedUiModel = connectedStatus.toUiModel()
        assertEquals(connectedServer.id, connectedUiModel.id)
        assertEquals(connectedServer.name, connectedUiModel.name)
        assertEquals(UIMCPServer.Status.Connected, connectedUiModel.status)

        // Test Offline status
        val offlineServer = McpServerConfig(
            id = "test-id-2",
            name = "Offline Test Server",
            connection = McpServerConfig.Connection.Stdio("test command")
        )
        val offlineStatus = McpServer(
            config = offlineServer,
            status = McpServer.Status.Offline,
            tools = emptyList()
        )

        val offlineUiModel = offlineStatus.toUiModel()
        assertEquals(offlineServer.id, offlineUiModel.id)
        assertEquals(offlineServer.name, offlineUiModel.name)
        assertEquals(UIMCPServer.Status.Disconnected, offlineUiModel.status)
    }
}
