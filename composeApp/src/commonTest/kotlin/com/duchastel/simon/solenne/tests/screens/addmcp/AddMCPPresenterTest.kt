package com.duchastel.simon.solenne.tests.screens.addmcp

import com.duchastel.simon.solenne.data.features.Features
import com.duchastel.simon.solenne.data.tools.McpServerConfig
import com.duchastel.simon.solenne.screens.addmcp.AddMCPPresenter
import com.duchastel.simon.solenne.screens.addmcp.AddMCPScreen
import com.duchastel.simon.solenne.util.expectNoNavigationEvents
import com.duchastel.simon.solenne.fakes.FakeMcpRepository
import com.duchastel.simon.solenne.screens.addmcp.AddMCPScreen.ServerConfig
import com.duchastel.simon.solenne.screens.addmcp.AddMCPScreen.ServerType
import com.duchastel.simon.solenne.util.fakes.FakeMcpRepository
import com.duchastel.simon.solenne.util.fakes.FakeFeatures
import com.slack.circuit.test.FakeNavigator
import com.slack.circuit.test.test
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AddMCPPresenterTest {

    private lateinit var navigator: FakeNavigator
    private lateinit var mcpRepository: FakeMcpRepository
    private lateinit var features: Features
    private lateinit var presenter: AddMCPPresenter

    @BeforeTest
    fun setup() {
        navigator = FakeNavigator(AddMCPScreen)
        mcpRepository = FakeMcpRepository()
        features = FakeFeatures()
        presenter = AddMCPPresenter(
            navigator = navigator,
            mcpRepository = mcpRepository,
            features = features,
        )
    }

    @Test
    fun `initial state - fields empty and save disabled`() = runTest {
        presenter.test {
            val state = expectMostRecentItem()
            assertEquals("", state.serverName)
            assertTrue(state.config is ServerConfig.Remote)
            assertEquals("", (state.config as ServerConfig.Remote).url)
            assertTrue(state.localMcpEnabled)
            assertNull(state.saveEnabled)
        }
    }

    @Test
    fun `serverName changed - updates state`() = runTest {
        presenter.test {
            val initialState = expectMostRecentItem()
            initialState.eventSink(AddMCPScreen.Event.ServerNameChanged("Test Server"))

            val updatedState = expectMostRecentItem()
            assertEquals("Test Server", updatedState.serverName)
            assertTrue(updatedState.config is ServerConfig.Remote)
            assertEquals("", (updatedState.config as ServerConfig.Remote).url)
            assertNull(updatedState.saveEnabled)
        }
    }

    @Test
    fun `remote server url changed - updates state`() = runTest {
        presenter.test {
            val initialState = expectMostRecentItem()
            val remoteConfig = initialState.config as ServerConfig.Remote
            remoteConfig.onUrlChanged("http://example.com")

            val updatedState = expectMostRecentItem()
            assertEquals("", updatedState.serverName)
            assertTrue(updatedState.config is ServerConfig.Remote)
            assertEquals("http://example.com", (updatedState.config as ServerConfig.Remote).url)
            assertNull(updatedState.saveEnabled)
        }
    }

    @Test
    fun `server type changed to local - updates config type`() = runTest {
        presenter.test {
            val initialState = expectMostRecentItem()
            initialState.eventSink(AddMCPScreen.Event.ServerTypeChanged(ServerType.LOCAL))

            val updatedState = expectMostRecentItem()
            assertTrue(updatedState.config is ServerConfig.Local)
            assertEquals("", (updatedState.config as ServerConfig.Local).command)
            assertTrue((updatedState.config as ServerConfig.Local).environmentVariables.isEmpty())
        }
    }

    @Test
    fun `local command changed - updates state`() = runTest {
        presenter.test {
            val initialState = expectMostRecentItem()
            initialState.eventSink(AddMCPScreen.Event.ServerTypeChanged(ServerType.LOCAL))
            val localState = expectMostRecentItem()

            val localConfig = localState.config as ServerConfig.Local
            localConfig.onCommandChanged("python server.py")

            val updatedState = expectMostRecentItem()
            assertTrue(updatedState.config is ServerConfig.Local)
            assertEquals("python server.py", (updatedState.config as ServerConfig.Local).command)
        }
    }

    @Test
    fun `local environment variable updated - updates state`() = runTest {
        presenter.test {
            val initialState = expectMostRecentItem()
            initialState.eventSink(AddMCPScreen.Event.ServerTypeChanged(ServerType.LOCAL))
            val localState = expectMostRecentItem()

            val localConfig = localState.config as ServerConfig.Local
            localConfig.onEnvironmentVariableUpdated("API_KEY", "test-key")

            val updatedState = expectMostRecentItem()
            assertTrue(updatedState.config is ServerConfig.Local)
            val envVars = (updatedState.config as ServerConfig.Local).environmentVariables
            assertEquals("test-key", envVars["API_KEY"])
        }
    }

    @Test
    fun `local environment variable removed - updates state`() = runTest {
        presenter.test {
            val initialState = expectMostRecentItem()
            initialState.eventSink(AddMCPScreen.Event.ServerTypeChanged(ServerType.LOCAL))
            val localState = expectMostRecentItem()

            val localConfig = localState.config as ServerConfig.Local
            localConfig.onEnvironmentVariableUpdated("API_KEY", "test-key")
            val withVarState = expectMostRecentItem()

            val configWithVar = withVarState.config as ServerConfig.Local
            configWithVar.onEnvironmentVariableUpdated("API_KEY", null)

            val finalState = expectMostRecentItem()
            assertTrue(finalState.config is ServerConfig.Local)
            val envVars = (finalState.config as ServerConfig.Local).environmentVariables
            assertFalse(envVars.containsKey("API_KEY"))
        }
    }

    @Test
    fun `remote server - both fields filled enables save`() = runTest {
        presenter.test {
            val initialState = expectMostRecentItem()

            initialState.eventSink(AddMCPScreen.Event.ServerNameChanged("Test Server"))
            val nameState = expectMostRecentItem()

            val remoteConfig = nameState.config as ServerConfig.Remote
            remoteConfig.onUrlChanged("http://example.com")
            val finalState = expectMostRecentItem()

            assertEquals("Test Server", finalState.serverName)
            assertTrue(finalState.config is ServerConfig.Remote)
            assertEquals("http://example.com", (finalState.config as ServerConfig.Remote).url)
            assertTrue(finalState.saveEnabled != null)
        }
    }

    @Test
    fun `local server - both fields filled enables save`() = runTest {
        presenter.test {
            val initialState = expectMostRecentItem()

            initialState.eventSink(AddMCPScreen.Event.ServerNameChanged("Test Server"))
            val nameState = expectMostRecentItem()

            nameState.eventSink(AddMCPScreen.Event.ServerTypeChanged(ServerType.LOCAL))
            val localState = expectMostRecentItem()

            val localConfig = localState.config as ServerConfig.Local
            localConfig.onCommandChanged("python server.py")
            val finalState = expectMostRecentItem()

            assertEquals("Test Server", finalState.serverName)
            assertTrue(finalState.config is ServerConfig.Local)
            assertEquals("python server.py", (finalState.config as ServerConfig.Local).command)
            assertTrue(finalState.saveEnabled != null)
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
    fun `save remote server - adds server and navigates back`() = runTest {
        presenter.test {
            val initialState = expectMostRecentItem()

            // Fill out form for remote server
            initialState.eventSink(AddMCPScreen.Event.ServerNameChanged("Test Server"))
            val nameState = expectMostRecentItem()

            val remoteConfig = nameState.config as ServerConfig.Remote
            remoteConfig.onUrlChanged("http://example.com")
            val readyState = expectMostRecentItem()

            // Save the form
            readyState.saveEnabled!!.onSavePressed("Test Server", readyState.config)

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
    fun `save local server - adds server and navigates back`() = runTest {
        presenter.test {
            val initialState = expectMostRecentItem()

            // Fill out form for local server
            initialState.eventSink(AddMCPScreen.Event.ServerNameChanged("Local Server"))
            val nameState = expectMostRecentItem()

            nameState.eventSink(AddMCPScreen.Event.ServerTypeChanged(ServerType.LOCAL))
            val localState = expectMostRecentItem()

            val localConfig = localState.config as ServerConfig.Local
            localConfig.onCommandChanged("python local_server.py")
            val readyState = expectMostRecentItem()

            // Save the form
            readyState.saveEnabled!!.onSavePressed("Local Server", readyState.config)

            // Verify navigation happened
            navigator.awaitPop()

            // Check server was added with correct values
            val servers = mcpRepository.getServers()
            assertEquals(1, servers.size)
            assertEquals("Local Server", servers[0].config.name)
            assertEquals(
                McpServerConfig.Connection.Stdio("python local_server.py"),
                servers[0].config.connection
            )

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `features disabled - localMcpEnabled is false`() = runTest {
        val disabledFeatures = FakeFeatures(localMcpServerAvailable = false)
        val presenterWithDisabledFeatures = AddMCPPresenter(
            navigator = navigator,
            mcpRepository = mcpRepository,
            features = disabledFeatures,
        )

        presenterWithDisabledFeatures.test {
            val state = expectMostRecentItem()
            assertFalse(state.localMcpEnabled)
        }
    }
}
