package com.duchastel.simon.solenne.screens.addmcp

import com.duchastel.simon.solenne.data.tools.McpServerConfig
import com.duchastel.simon.solenne.util.expectNoNavigationEvents
import com.duchastel.simon.solenne.util.fakes.FakeMcpRepository
import com.slack.circuit.test.FakeNavigator
import com.slack.circuit.test.test
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AddMCPPresenterTest {

    private lateinit var navigator: FakeNavigator
    private lateinit var mcpRepository: FakeMcpRepository
    private lateinit var presenter: AddMCPPresenter

    @BeforeTest
    fun setup() {
        navigator = FakeNavigator(AddMCPScreen)
        mcpRepository = FakeMcpRepository()
        presenter = AddMCPPresenter(
            navigator = navigator,
            mcpRepository = mcpRepository,
        )
    }

    @Test
    fun `initial state - fields empty and save disabled`() = runTest {
        presenter.test {
            val state = expectMostRecentItem()
            assertEquals("", state.serverName)
            assertEquals("", state.serverUrl)
            assertFalse(state.isSaveEnabled)
        }
    }

    @Test
    fun `serverName changed - updates state`() = runTest {
        presenter.test {
            val initialState = expectMostRecentItem()
            initialState.eventSink(AddMCPScreen.Event.ServerNameChanged("Test Server"))

            val updatedState = expectMostRecentItem()
            assertEquals("Test Server", updatedState.serverName)
            assertEquals("", updatedState.serverUrl)
            assertFalse(updatedState.isSaveEnabled)
        }
    }

    @Test
    fun `serverUrl changed - updates state`() = runTest {
        presenter.test {
            val initialState = expectMostRecentItem()
            initialState.eventSink(AddMCPScreen.Event.ServerUrlChanged("http://example.com"))

            val updatedState = expectMostRecentItem()
            assertEquals("", updatedState.serverName)
            assertEquals("http://example.com", updatedState.serverUrl)
            assertFalse(updatedState.isSaveEnabled)
        }
    }

    @Test
    fun `both fields filled - enables save button`() = runTest {
        presenter.test {
            val initialState = expectMostRecentItem()

            initialState.eventSink(AddMCPScreen.Event.ServerNameChanged("Test Server"))
            val nameState = expectMostRecentItem()

            nameState.eventSink(AddMCPScreen.Event.ServerUrlChanged("http://example.com"))
            val finalState = expectMostRecentItem()

            assertEquals("Test Server", finalState.serverName)
            assertEquals("http://example.com", finalState.serverUrl)
            assertTrue(finalState.isSaveEnabled)
        }
    }

    @Test
    fun `back pressed - navigates back`() = runTest {
        presenter.test {
            val state = expectMostRecentItem()
            state.eventSink(AddMCPScreen.Event.BackPressed)

            navigator.awaitPop()

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `save pressed - adds server and navigates back`() = runTest {
        presenter.test {
            val initialState = expectMostRecentItem()

            // Fill out form
            initialState.eventSink(AddMCPScreen.Event.ServerNameChanged("Test Server"))
            val nameState = expectMostRecentItem()

            nameState.eventSink(AddMCPScreen.Event.ServerUrlChanged("http://example.com"))
            val readyState = expectMostRecentItem()

            // Save the form
            readyState.eventSink(AddMCPScreen.Event.SavePressed)

            // Verify navigation happened
            navigator.awaitPop()

            // Check server was added with correct values
            val servers = mcpRepository.getServers()
            assertEquals(1, servers.size)
            assertEquals("Test Server", servers[0].config.name)
            assertEquals(
                McpServerConfig.Connection.Sse("http://example.com"),
                servers[0].config.connection
            )

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `save pressed - does nothing when not enabled`() = runTest {
        presenter.test {
            val initialState = expectMostRecentItem()
            initialState.eventSink(AddMCPScreen.Event.SavePressed)

            // No server should be added
            assertTrue(mcpRepository.getServers().isEmpty())
            navigator.expectNoNavigationEvents()

            cancelAndConsumeRemainingEvents()
        }
    }
}